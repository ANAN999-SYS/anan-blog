package xyz.kuailemao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import xyz.kuailemao.constants.RedisConst;
import xyz.kuailemao.constants.SQLConst;
import xyz.kuailemao.domain.dto.FavoriteIsCheckDTO;
import xyz.kuailemao.domain.dto.SearchFavoriteDTO;
import xyz.kuailemao.domain.entity.*;
import xyz.kuailemao.domain.response.ResponseResult;
import xyz.kuailemao.domain.vo.*;
import xyz.kuailemao.enums.CommentEnum;
import xyz.kuailemao.enums.CountTypeEnum;
import xyz.kuailemao.enums.LikeEnum;
import xyz.kuailemao.mapper.*;
import xyz.kuailemao.service.ArticleService;
import xyz.kuailemao.service.CategoryService;
import xyz.kuailemao.service.FavoriteService;
import xyz.kuailemao.service.LeaveWordService;
import xyz.kuailemao.utils.RedisCache;
import xyz.kuailemao.utils.SecurityUtils;
import xyz.kuailemao.utils.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * (Favorite)表服务实现类
 *
 * @author kuailemao
 * @since 2023-10-18 14:12:25
 */
@Service("favoriteService")
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {

    @Resource
    private FavoriteMapper favoriteMapper;

    @Resource
    private RedisCache redisCache;

    @Resource
    private UserMapper userMapper;

    @Resource
    private ArticleMapper articleMapper;

    @Resource
    private LeaveWordMapper leaveWordMapper;

    @Override
    public ResponseResult<Void> userFavorite(Integer type, Long typeId) {
        // 查询是否已经收藏
        Favorite favorite = favoriteMapper.selectOne(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, SecurityUtils.getUserId())
                .eq(Favorite::getType, type)
                .eq(Favorite::getTypeId, typeId));
        if (StringUtils.isNotNull(favorite)) return ResponseResult.failure();
        Favorite Savefavorite = Favorite.builder()
                .id(null)
                .userId(SecurityUtils.getUserId())
                .type(type)
                .typeId(typeId).build();
        redisCache.incrementCacheMapValue(RedisConst.ARTICLE_FAVORITE_COUNT, typeId.toString(), 1);
        if (this.save(Savefavorite)) return ResponseResult.success();
        return ResponseResult.failure();
    }

    @Override
    public ResponseResult<Void> cancelFavorite(Integer type, Integer typeId) {
        // 查询是否已经收藏
        Favorite isFavorite = favoriteMapper.selectOne(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, SecurityUtils.getUserId())
                .eq(Favorite::getType, type)
                .eq(Favorite::getTypeId, typeId));
        if (Objects.isNull(isFavorite)) return ResponseResult.failure();
        boolean cancelFavorite = this.remove(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, SecurityUtils.getUserId())
                .eq(Favorite::getType, type)
                .eq(Favorite::getTypeId, typeId));
        redisCache.incrementCacheMapValue(RedisConst.ARTICLE_FAVORITE_COUNT, typeId.toString(), -1);
        if (cancelFavorite) return ResponseResult.success();
        return ResponseResult.failure();
    }

    @Override
    public Boolean isFavorite(Integer type, Integer typeId) {
        if (SecurityUtils.isLogin()) {
            // 是否收藏
            Favorite favorite = favoriteMapper.selectOne(new LambdaQueryWrapper<Favorite>()
                    .eq(Favorite::getUserId, SecurityUtils.getUserId())
                    .eq(Favorite::getType, type)
                    .eq(Favorite::getTypeId, typeId));
            return favorite != null;
        }
        return false;
    }

    @Override
    public List<FavoriteListVO> getBackFavoriteList(SearchFavoriteDTO searchDTO) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotNull(searchDTO)) {
            // 搜索
            List<User> users = userMapper.selectList(new LambdaQueryWrapper<User>().like(User::getUsername, searchDTO.getUserName()));
            if (!users.isEmpty())
                wrapper.in(StringUtils.isNotEmpty(searchDTO.getUserName()), Favorite::getUserId, users.stream().map(User::getId).collect(Collectors.toList()));
            else
                wrapper.eq(StringUtils.isNotNull(searchDTO.getUserName()), Favorite::getUserId, null);

            wrapper.eq(StringUtils.isNotNull(searchDTO.getIsCheck()), Favorite::getIsCheck, searchDTO.getIsCheck())
                    .eq(StringUtils.isNotNull(searchDTO.getType()), Favorite::getType, searchDTO.getType());
            if (StringUtils.isNotNull(searchDTO.getStartTime()) && StringUtils.isNotNull(searchDTO.getEndTime()))
                wrapper.between(Favorite::getCreateTime, searchDTO.getStartTime(), searchDTO.getEndTime());
        }
        List<Favorite> favorites = favoriteMapper.selectList(wrapper);
        if (!favorites.isEmpty()) {
            return favorites.stream().map(favorite -> favorite.asViewObject(FavoriteListVO.class,
                    v -> {
                        v.setUserName(userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getId, favorite.getUserId())).getUsername());
                        switch (favorite.getType()) {
                            case 1 -> v.setContent(articleMapper.selectById(favorite.getTypeId()).getArticleContent());
                            case 2 -> v.setContent(leaveWordMapper.selectById(favorite.getTypeId()).getContent());
                        }
                    })).toList();
        }
        return null;
    }
    @Resource
    private CategoryMapper categoryMapper;
    @Resource
    private TagMapper tagMapper;
    @Resource
    private ArticleTagMapper articleTagMapper;
    @Override
    public PageVO<List<ArticleVO>> getFavoriteArticleList(SearchFavoriteDTO searchDTO, Integer pageNum, Integer pageSize) {
        // 初始化分页参数，设置默认值
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 10 : pageSize;

        // 构造分页对象
        Page<Favorite> page = new Page<>(pageNum, pageSize);

        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();

        if (searchDTO != null) {
            // 必须指定用户ID
            if (searchDTO.getUserId() != null) {
                wrapper.eq(Favorite::getUserId, searchDTO.getUserId());
            } else {
                // 如果没有用户ID，返回空分页
                return new PageVO<>(null, 0L);
            }

            // 只查询文章类型的收藏 (类型1为文章)
            wrapper.eq(Favorite::getType, searchDTO.getType());

            // 其他筛选条件
            wrapper.eq(StringUtils.isNotNull(searchDTO.getIsCheck()), Favorite::getIsCheck, searchDTO.getIsCheck());

            // 时间范围筛选
            if (searchDTO.getStartTime() != null && searchDTO.getEndTime() != null) {
                wrapper.between(Favorite::getCreateTime, searchDTO.getStartTime(), searchDTO.getEndTime());
            }
        } else {
            // 如果没有查询条件，返回空分页
            return new PageVO<>(null, 0L);
        }

        // 按收藏时间倒序排列，最新收藏的在前
        wrapper.orderByDesc(Favorite::getCreateTime);

        // 执行分页查询，获取用户收藏的文章记录
        Page<Favorite> favoritePage = favoriteMapper.selectPage(page, wrapper);

        // 转换为文章VO对象列表
        List<ArticleVO> voList = favoritePage.getRecords().stream()
                .map(favorite -> {
                    // 直接通过mapper查询文章，不再依赖ArticleService
                    Article article = articleMapper.selectById(favorite.getTypeId());
                    if (article == null) {
                        return null;
                    }

                    // 转换为VO对象
                    ArticleVO articleVO = article.asViewObject(ArticleVO.class);

                    // 设置分类名称
                    Category category = categoryMapper.selectById(article.getCategoryId());
                    if (category != null) {
                        articleVO.setCategoryName(category.getCategoryName());
                    }

                    // 设置标签
                    List<ArticleTag> articleTags = articleTagMapper.selectList(
                            new LambdaQueryWrapper<ArticleTag>().eq(ArticleTag::getArticleId, article.getId())
                    );
                    if (!articleTags.isEmpty()) {
                        List<Long> tagIds = articleTags.stream().map(ArticleTag::getTagId).toList();
                        List<Tag> tags = tagMapper.selectBatchIds(tagIds);
                        articleVO.setTags(tags.stream().map(Tag::getTagName).toList());
                    }

                    // 设置计数信息
                    boolean hasKey = redisCache.isHasKey(RedisConst.ARTICLE_COMMENT_COUNT) &&
                            redisCache.isHasKey(RedisConst.ARTICLE_FAVORITE_COUNT) &&
                            redisCache.isHasKey(RedisConst.ARTICLE_LIKE_COUNT);

                    if (hasKey) {
                        setArticleCount(articleVO, RedisConst.ARTICLE_FAVORITE_COUNT, CountTypeEnum.FAVORITE);
                        setArticleCount(articleVO, RedisConst.ARTICLE_LIKE_COUNT, CountTypeEnum.LIKE);
                        setArticleCount(articleVO, RedisConst.ARTICLE_COMMENT_COUNT, CountTypeEnum.COMMENT);
                    }

                    return articleVO;
                })
                .filter(Objects::nonNull) // 过滤掉可能的空值
                .collect(Collectors.toList());

        // 构建返回分页对象
        return new PageVO<>(voList, favoritePage.getTotal());
    }
    @Resource
    private CommentMapper commentMapper;
    @Resource
    private LikeMapper likeMapper;
    @Override
    public PageVO<List<LeaveWordVO>> getFavoriteLeaveWordVOList(SearchFavoriteDTO searchDTO, Integer pageNum, Integer pageSize) {
        // 初始化分页参数，设置默认值
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 10 : pageSize;

        // 构造分页对象
        Page<Favorite> page = new Page<>(pageNum, pageSize);

        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();

        if (searchDTO != null) {
            // 必须指定用户ID
            if (searchDTO.getUserId() != null) {
                wrapper.eq(Favorite::getUserId, searchDTO.getUserId());
            } else {
                // 如果没有用户ID，返回空分页
                return new PageVO<>(null, 0L);
            }

            // 只查询文章类型的收藏 (类型1为文章)
            wrapper.eq(Favorite::getType, searchDTO.getType());

            // 其他筛选条件
            wrapper.eq(StringUtils.isNotNull(searchDTO.getIsCheck()), Favorite::getIsCheck, searchDTO.getIsCheck());

            // 时间范围筛选
            if (searchDTO.getStartTime() != null && searchDTO.getEndTime() != null) {
                wrapper.between(Favorite::getCreateTime, searchDTO.getStartTime(), searchDTO.getEndTime());
            }
        } else {
            // 如果没有查询条件，返回空分页
            return new PageVO<>(null, 0L);
        }

        // 按收藏时间倒序排列，最新收藏的在前
        wrapper.orderByDesc(Favorite::getCreateTime);

        // 执行分页查询，获取用户收藏的文章记录
        Page<Favorite> favoritePage = favoriteMapper.selectPage(page, wrapper);
        List<LeaveWordVO> voList = favoritePage.getRecords().stream()
                .map(favorite -> {
                    LeaveWord leaveWord = leaveWordMapper.selectOne(new LambdaQueryWrapper<LeaveWord>().eq(LeaveWord::getId, favorite.getTypeId()).eq(LeaveWord::getIsCheck, SQLConst.IS_CHECK_YES));
                    // 如果没有查询到留言，返回null
                    if (leaveWord == null) {
                        return null;
                    }
                    LeaveWordVO leaveWordVO = leaveWord.asViewObject(LeaveWordVO.class);
                    User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getId,searchDTO.getUserId()));
                    leaveWordVO.setNickname(user.getNickname())
                            .setAvatar(user.getAvatar())
                            .setCommentCount(commentMapper.selectCount(new LambdaQueryWrapper<Comment>().eq(Comment::getType, CommentEnum.COMMENT_TYPE_LEAVE_WORD.getType()).eq(Comment::getIsCheck, SQLConst.IS_CHECK_YES).eq(Comment::getTypeId, leaveWord.getId())))
                            .setLikeCount(likeMapper.selectCount(new LambdaQueryWrapper<Like>().eq(Like::getType, LikeEnum.LIKE_TYPE_LEAVE_WORD.getType()).eq(Like::getTypeId, leaveWord.getId())))
                            .setFavoriteCount(favoriteMapper.selectCount(new LambdaQueryWrapper<Favorite>().eq(Favorite::getType, CommentEnum.COMMENT_TYPE_LEAVE_WORD.getType()).eq(Favorite::getTypeId, leaveWord.getId())));
                    // 检查列表是否为空，避免访问不存在的元素
                    return leaveWordVO;
                })
                .filter(Objects::nonNull) // 过滤掉可能的空值
                .collect(Collectors.toList());
        return new PageVO<>(voList, favoritePage.getTotal());
    }
    // 提取计数设置方法为独立方法，供内部使用
    private void setArticleCount(ArticleVO articleVO, String redisKey, CountTypeEnum articleFieldName) {
        String articleId = articleVO.getId().toString();
        Object countObj = redisCache.getCacheMap(redisKey).get(articleId);
        long count = 0L;
        if (countObj != null) {
            count = Long.parseLong(countObj.toString());
        } else {
            // 缓存发布新文章时数量缓存不存在
            redisCache.setCacheMap(redisKey, Map.of(articleId, 0));
        }

        if (articleFieldName.equals(CountTypeEnum.FAVORITE)) {
            articleVO.setFavoriteCount(count);
        } else if (articleFieldName.equals(CountTypeEnum.LIKE)) {
            articleVO.setLikeCount(count);
        } else if (articleFieldName.equals(CountTypeEnum.COMMENT)) {
            articleVO.setCommentCount(count);
        }
    }

    @Override
    public ResponseResult<Void> isCheckFavorite(FavoriteIsCheckDTO isCheckDTO) {
        if (favoriteMapper.updateById(Favorite.builder().id(isCheckDTO.getId()).isCheck(isCheckDTO.getIsCheck()).build()) > 0)
            return ResponseResult.success();
        return ResponseResult.failure();
    }

    @Override
    public ResponseResult<Void> deleteFavorite(List<Long> ids) {
        if (favoriteMapper.deleteBatchIds(ids) > 0) return ResponseResult.success();
        return ResponseResult.failure();
    }
}
