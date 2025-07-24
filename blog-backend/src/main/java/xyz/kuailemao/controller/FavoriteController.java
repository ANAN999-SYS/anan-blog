package xyz.kuailemao.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import xyz.kuailemao.annotation.AccessLimit;
import xyz.kuailemao.annotation.CheckBlacklist;
import xyz.kuailemao.annotation.LogAnnotation;
import xyz.kuailemao.constants.LogConst;
import xyz.kuailemao.domain.dto.FavoriteIsCheckDTO;
import xyz.kuailemao.domain.dto.SearchFavoriteDTO;
import xyz.kuailemao.domain.response.ResponseResult;
import xyz.kuailemao.domain.vo.ArticleVO;
import xyz.kuailemao.domain.vo.FavoriteListVO;
import xyz.kuailemao.domain.vo.LeaveWordVO;
import xyz.kuailemao.domain.vo.PageVO;
import xyz.kuailemao.service.FavoriteService;
import xyz.kuailemao.utils.ControllerUtils;
import xyz.kuailemao.utils.JwtUtils;

import java.util.List;

/**
 * @author kuailemao
 * <p>
 * 创建时间：2023/11/3 10:16
 */
@RestController
@Tag(name = "收藏相关接口")
@RequestMapping("/favorite")
@Validated
public class FavoriteController {

    @Resource
    private FavoriteService favoriteService;

    @CheckBlacklist
    @Operation(summary = "收藏")
    @Parameters({
            @Parameter(name = "type", description = "收藏类型", required = true),
            @Parameter(name = "typeId", description = "收藏id", required = true)
    })
    @AccessLimit(seconds = 60, maxCount = 10)
    @PostMapping("/auth/favorite")
    public ResponseResult<Void> favorite(
            @Valid @NotNull @RequestParam("type") Integer type,
            @RequestParam(value = "typeId", required = false) Long typeId
    ) {
        return favoriteService.userFavorite(type, typeId);
    }

    @CheckBlacklist
    @Operation(summary = "取消收藏")
    @Parameters({
            @Parameter(name = "type", description = "收藏类型", required = true),
            @Parameter(name = "typeId", description = "收藏id", required = true)
    })
    @AccessLimit(seconds = 60, maxCount = 10)
    @DeleteMapping("/auth/favorite")
    public ResponseResult<Void> cancelFavorite(
            @Valid @NotNull @RequestParam("type") Integer type,
            @RequestParam(value = "typeId", required = false) Integer typeId
    ) {
        return favoriteService.cancelFavorite(type, typeId);
    }

    @Operation(summary = "是否已经收藏")
    @Parameters({
            @Parameter(name = "type", description = "收藏类型", required = true),
            @Parameter(name = "typeId", description = "收藏id", required = true)
    })
    @AccessLimit(seconds = 60, maxCount = 60)
    @GetMapping("/whether/favorite")
    public ResponseResult<Boolean> isFavorite(
            @Valid @NotNull @RequestParam("type") Integer type,
            @RequestParam(value = "typeId", required = false) Integer typeId
    ) {
        return ControllerUtils.messageHandler((() -> favoriteService.isFavorite(type, typeId)));
    }
    @Autowired
    private JwtUtils jwtUtils;
    @Operation(summary = "用户收藏")
    @AccessLimit(seconds = 60, maxCount = 30)
    @LogAnnotation(module="用户收藏",operation= LogConst.GET)
    @Parameters({
            @Parameter(name = "pageNum", description = "页码", required = true),
            @Parameter(name = "pageSize", description = "每页数量", required = true)
    })
    @GetMapping("/favoriteArticleList")
    public ResponseResult<PageVO<List<ArticleVO>>> favoriteArticleList(
            @Valid @NotNull @RequestParam("pageNum") Integer pageNum,
            @Valid @NotNull @RequestParam("pageSize") Integer pageSize,
            @RequestHeader("authorization") String token
    ) {
        Long userId = jwtUtils.toId(jwtUtils.resolveJwt(token));
        SearchFavoriteDTO searchDTO = new SearchFavoriteDTO();
        searchDTO.setUserId(userId);
        searchDTO.setType(1);
        searchDTO.setIsCheck(1);
        return ControllerUtils.messageHandler(() -> favoriteService.getFavoriteArticleList(searchDTO, pageNum, pageSize));
    }
    @GetMapping("/favoriteLeaveWordList")
    public ResponseResult<PageVO<List<LeaveWordVO>>> favoriteLeaveWordList(
            @Valid @NotNull @RequestParam("pageNum") Integer pageNum,
            @Valid @NotNull @RequestParam("pageSize") Integer pageSize,
            @RequestHeader("authorization") String token
    ) {
        Long userId = jwtUtils.toId(jwtUtils.resolveJwt(token));
        SearchFavoriteDTO searchDTO = new SearchFavoriteDTO();
        searchDTO.setUserId(userId);
        searchDTO.setType(2);
        searchDTO.setIsCheck(1);
        return ControllerUtils.messageHandler(() -> favoriteService.getFavoriteLeaveWordVOList(searchDTO, pageNum, pageSize));
    }
    @PreAuthorize("hasAnyAuthority('blog:favorite:list')")
    @Operation(summary = "后台收藏列表")
    @AccessLimit(seconds = 60, maxCount = 30)
    @LogAnnotation(module="收藏管理",operation= LogConst.GET)
    @GetMapping("/back/list")
    public ResponseResult<List<FavoriteListVO>> backList() {
        return ControllerUtils.messageHandler(() -> favoriteService.getBackFavoriteList(null));
    }

    @PreAuthorize("hasAnyAuthority('blog:favorite:search')")
    @Operation(summary = "搜索后台收藏列表")
    @AccessLimit(seconds = 60, maxCount = 30)
    @LogAnnotation(module="收藏管理",operation= LogConst.SEARCH)
    @PostMapping("/back/search")
    public ResponseResult<List<FavoriteListVO>> backList(@RequestBody SearchFavoriteDTO searchDTO) {
        return ControllerUtils.messageHandler(() -> favoriteService.getBackFavoriteList(searchDTO));
    }

    @PreAuthorize("hasAnyAuthority('blog:favorite:isCheck')")
    @Operation(summary = "修改收藏是否通过")
    @AccessLimit(seconds = 60, maxCount = 30)
    @LogAnnotation(module="收藏管理",operation= LogConst.UPDATE)
    @PostMapping("/back/isCheck")
    public ResponseResult<Void> isCheck(@RequestBody @Valid FavoriteIsCheckDTO favoriteIsCheckDTO) {
        return favoriteService.isCheckFavorite(favoriteIsCheckDTO);
    }

    @PreAuthorize("hasAnyAuthority('blog:favorite:delete')")
    @Operation(summary = "删除收藏")
    @AccessLimit(seconds = 60, maxCount = 30)
    @LogAnnotation(module="收藏管理",operation= LogConst.DELETE)
    @DeleteMapping("/back/delete")
    public ResponseResult<Void> delete(@RequestBody List<Long> ids) {
        return favoriteService.deleteFavorite(ids);
    }

}
