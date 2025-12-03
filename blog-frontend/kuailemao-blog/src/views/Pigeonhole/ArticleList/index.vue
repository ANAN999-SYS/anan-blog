<script setup lang="ts">
defineProps({
  articleList: {
    type: Array,
    required: true
  }
})
</script>

<template>
  <div class="category_article_container">
    <template v-for="article in articleList" :key="article.id">
      <!-- 外层flex容器，保证左右高度一致 -->
      <div class="article-item" @click="$router.push(`/article/${article.id}`)">
        <div class="article-left">
          <!-- 去掉aspectRatio，用基础属性保证加载 -->
          <img :src="article.articleCover" alt="文章预览图" class="article-cover">
        </div>
        <div class="article-right">
          <div class="article-header">
            <span class="article-time">{{ article.createTime }}</span>
            <div class="article-title-row">
              <h3 class="article-title">{{ article.articleTitle }}</h3>
              <span class="article-visit">
                <SvgIcon name="heat" class="visit-icon"/> {{ article.visitCount }}
              </span>
            </div>
          </div>
          <div class="article-tag-wrap">
            <template v-for="tag in article.tags" :key="tag.id">
              <span class="article-tag" @click.stop="$router.push(`/tags/${tag.id}`)">
                #{{ tag.tagName }}
              </span>
            </template>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<style scoped lang="scss">
.category_article_container {
  padding: 1.5rem 0;
  display: flex;
  flex-wrap: wrap;
  gap: 1.8rem;
  width: 100%;
  box-sizing: border-box;
}

.article-item {
  display: flex;
  align-items: center; /* 改为居中对齐，避免高度拉伸导致的异常 */
  width: calc(50% - 0.9rem);
  cursor: pointer;
  box-sizing: border-box;
  transition: all 0.3s ease;

  @media screen and (max-width: 1100px) {
    width: 100%;
  }

  &:hover {
    transform: translateY(-2px);
  }
}

/* 图片容器：固定宽度，高度自适应图片比例 */
.article-left {
  width: 180px; /* 适当放大宽度，视觉更协调 */
  border-radius: 0.6rem;
  overflow: hidden;
  border: 2px solid #862e9c;
  flex-shrink: 0;
  transition: border-color 0.3s ease;

  .article-item:hover & {
    border-color: #a74ccc;
  }
}

/* 核心：图片按自身比例缩放，高度跟随内容居中对齐 */
.article-cover {
  width: 100%;
  height: auto; /* 高度自动，保持图片原始比例 */
  max-height: 120px; /* 限制最大高度，避免图片过高 */
  object-fit: cover; /* 保证图片填充容器，不拉伸 */
  transition: transform 0.3s linear;

  .article-item:hover & {
    transform: scale(1.08);
  }
}

.article-right {
  display: flex;
  flex-direction: column;
  margin-left: 1.2rem;
  width: calc(100% - 192px); /* 适配图片宽度 */
}

.article-header {
  width: 100%;
}

.article-time {
  font-size: 0.95rem;
  color: #999;
  transition: color 0.3s ease;

  .article-item:hover & {
    color: #777;
  }
}

.article-title-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-top: 0.4rem;
}

.article-title {
  font-size: 1.35rem;
  font-weight: 600;
  color: #333;
  margin: 0;
  line-height: 1.5;
  max-width: calc(100% - 65px);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  transition: color 0.3s ease;

  .article-item:hover & {
    color: #862e9c;
  }
}

.article-visit {
  font-size: 0.9rem;
  color: #999;
  display: flex;
  align-items: center;
  gap: 0.4rem;
  margin-left: 0.5rem;
  flex-shrink: 0;
  transition: color 0.3s ease;

  .article-item:hover & {
    color: #777;
  }
}

.visit-icon {
  width: 16px;
  height: 16px;
}

.article-tag-wrap {
  display: flex;
  flex-wrap: wrap;
  gap: 0.6rem;
  margin-top: 0.6rem;
}

.article-tag {
  font-size: 0.95rem;
  color: #999;
  padding: 0.1rem 0;
  transition: color 0.3s ease;

  &:hover {
    color: #862e9c;
  }
}
</style>
