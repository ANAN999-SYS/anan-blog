<script setup lang="ts">
import {ElMessage} from "element-plus";
import {useWindowSize} from "@vueuse/core";
import usePaginationStore from "@/store/modules/pagination.ts";
import {getArticleList} from "@/apis/home";

const articleList = ref([]);

const paginationStore = usePaginationStore();

// 监听页数
watch(() => paginationStore.articlePagination.current, () => {
  getArticleListFunc()
  // 滚动到顶部
  window.scrollTo(0, 300);
})
const isGetArticleList = ref(true)
// 屏幕宽度
const { width } = useWindowSize()

function getArticleListFunc() {
  articleList.value=[]
  isGetArticleList.value = true
  getArticleList(paginationStore.articlePagination.current, paginationStore.articlePagination.pageSize).then(res => {
    if (res.code === 200) {
      isGetArticleList.value = false;
      paginationStore.articlePagination.total = res.data.total;
      // 过滤内容
      res.data.page = res.data.page.map((item: any) => {
        item.articleContent = item.articleContent.replace(/[*#>`~\-\\[\]()\s]|(\n\n)/g, '')
        // 提取前 50 个字符
        item.articleContent = item.articleContent.substring(0, 60) + '...';
        return item;
      });
      articleList.value = res.data.page;
    } else {
      ElMessage.error(res.msg);
    }
  })
}
function loadContent() {
  getArticleListFunc()
}
</script>

<template>
  <!-- 封装文章列表卡片 -->
  <div v-view-request="{ callback: loadContent }">
    <template v-for="(article,index) in articleList" :key="article.id" v-if="articleList.length > 0">
      <!-- 卡片容器：保留原有深色类和优化的悬浮动画 -->
      <div
          v-slide-in
          @click="$router.push('/article/'+article.id)"
          class="h-92 md:h-60 mt-4 flex flex-col md:flex-row card overflow-hidden shadow-md mb-5 mx-2 bg-white border border-gray-100 dark:bg-[#1D1D1D] dark:border-gray-800 hover:border-[#409EFF]/30 dark:hover:border-[#409EFF]/50 hover:shadow-xl transition-all duration-300 ease-out transform hover:-translate-y-1 cursor-pointer relative"
      >
        <!-- 顶部渐变装饰条（hover 时显示） -->
        <div class="absolute top-0 left-0 w-full h-1 bg-gradient-to-r from-[#409EFF] to-[#667eea] scale-x-0 hover:scale-x-100 transition-transform duration-500 origin-left z-10"></div>

        <!-- 左侧/右侧图片区域：恢复第一版封面分类标签样式 -->
        <div class="w-full md:h-full md:w-1/2 h-40" v-if="index % 2 == 1 || width < 768">
          <div class="relative w-full h-full">
            <div class="absolute inset-0 bg-gradient-to-t from-black/30 to-transparent hover:from-black/40 dark:from-black/40 dark:hover:from-black/50 transition-all duration-300 z-0"></div>
            <div class="relative img w-full h-full">
              <img class="w-full h-full object-cover hover:scale-110 ease-linear duration-500" v-lazy="true" :data-src="article.articleCover" alt="文章封面">
            </div>
            <!-- 封面分类标签：完全恢复第一版样式 -->
            <div class="classify text-white text-xs p-1.5 w-11 absolute top-0 left-0 rounded-br-lg">
              {{ article.categoryName }}
            </div>
          </div>
        </div>

        <!-- 内容区域：文章标签和摘要保持第一版样式 -->
        <div class="md:w-1/2 w-full m-1 px-2 flex flex-col pl-5 pt-2 leading-7">
          <!-- 标题保留优化的渐变动画 -->
          <div class="text-2xl font-bold w-fit bg-clip-text text-transparent bg-gradient-to-r from-[#2d3748] to-[#4a5568] dark:from-[#f7fafc] dark:to-[#e2e8f0] hover:bg-gradient-to-r hover:from-[#409EFF] hover:to-[#667eea] dark:hover:from-[#409EFF] dark:hover:to-[#667eea] transition-all duration-300">
            {{ article.articleTitle }}
          </div>

          <!-- 统计图标区域保留优化的 hover 效果 -->
          <div class="flex text-xs mt-2 space-x-2">
            <div class="flex text-gray-500 dark:text-gray-400 hover:text-[#409EFF] dark:hover:text-[#409EFF] transition-colors duration-300">
              <SvgIcon name="reading" class="w-4 h-4 mr-1 hover:fill-[#409EFF] dark:hover:fill-[#409EFF] transition-fill duration-300"/>
              <span class="ml-1">{{ article.visitCount }}</span>
            </div>
            <div class="flex text-gray-500 dark:text-gray-400 hover:text-[#409EFF] dark:hover:text-[#409EFF] transition-colors duration-300">
              <SvgIcon name="comments" class="w-4 h-4 mr-1 hover:fill-[#409EFF] dark:hover:fill-[#409EFF] transition-fill duration-300"/>
              <span class="ml-1">{{ article.commentCount }}</span>
            </div>
            <div class="flex text-gray-500 dark:text-gray-400 hover:text-[#409EFF] dark:hover:text-[#409EFF] transition-colors duration-300">
              <SvgIcon name="like" class="w-4 h-4 mr-1 hover:fill-[#409EFF] dark:hover:fill-[#409EFF] transition-fill duration-300"/>
              <span class="ml-1">{{ article.likeCount }}</span>
            </div>
            <div class="flex text-gray-500 dark:text-gray-400 hover:text-[#409EFF] dark:hover:text-[#409EFF] transition-colors duration-300">
              <SvgIcon name="collection" class="w-4 h-4 mr-1 hover:fill-[#409EFF] dark:hover:fill-[#409EFF] transition-fill duration-300"/>
              <span class="ml-1">{{ article.favoriteCount }}</span>
            </div>
          </div>

          <!-- 摘要：第一版样式（无背景、无圆角、无缩放） -->
          <p class="overflow-ellipsis overflow-hidden h-10 md:h-[3.9rem] leading-tight mt-2 text-[#475569] dark:text-[#a0aec0] hover:text-[#2d3748] dark:hover:text-[#e2e8f0] transition-colors duration-300">
            {{ article.articleContent }}
          </p>

          <!-- 文章标签：第一版样式 -->
          <div class="flex space-x-2 mt-2">
            <div class="tag text-gray-500 dark:text-gray-400">
              <span>标签：</span>
              <el-tag
                  size="small"
                  class="mr-2 hover:bg-[#409EFF] hover:text-white dark:hover:bg-[#409EFF] transition-all duration-300 cursor-pointer"
                  v-for="tag in article.tags"
              >
                {{ tag }}
              </el-tag>
            </div>
          </div>

          <!-- 时间保留优化的 hover 效果 -->
          <div class="text-xs mt-4 flex text-gray-500 dark:text-gray-400 hover:text-[#409EFF] dark:hover:text-[#409EFF] transition-colors duration-300">
            <p class="mr-4 mb-1">发布于：{{ article.createTime }}</p>
            <p>更新于：{{ article.updateTime }}</p>
          </div>
        </div>

        <!-- 右侧图片区域：恢复第一版封面分类标签样式 -->
        <div class="w-full md:h-full md:w-1/2 h-40" v-if="index % 2 == 0 && width > 768">
          <div class="relative w-full h-full">
            <div class="absolute inset-0 bg-gradient-to-t from-black/30 to-transparent hover:from-black/40 dark:from-black/40 dark:hover:from-black/50 transition-all duration-300 z-0"></div>
            <div class="relative img w-full h-full">
              <img class="w-full h-full object-cover hover:scale-110 ease-linear duration-500" v-lazy="true" :data-src="article.articleCover" alt="文章封面">
            </div>
            <!-- 封面分类标签：完全恢复第一版样式 -->
            <div class="classify text-white text-xs p-1.5 w-11 absolute top-0 left-0 rounded-br-lg">
              {{ article.categoryName }}
            </div>
          </div>
        </div>
      </div>
    </template>
  </div>
  <template v-if="articleList.length == 0&&isGetArticleList">
    <el-skeleton :rows="8" animated />
  </template>
  <template v-if="!isGetArticleList&&articleList.length == 0">
    <div style="display:flex;justify-content:center;align-items:center;height:88px">
      <div>此处啥也没有~~</div>
    </div>
  </template>
</template>

<style scoped lang="scss">
.card {
  border-radius: $border-radius;
  overflow: hidden;
  position: relative;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);

  .img {
    position: relative;
    overflow: hidden;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.5s cubic-bezier(0.25, 0.1, 0.25, 1);
    }
  }

  .classify {
    z-index: 1;
    position: absolute;
    top: 0;
    left: 0;
    color: white;
    padding: 10px;
    backdrop-filter: blur(5px);
    background: rgba(0, 0, 0, 0.3);
    border-radius: 0 0 $border-radius 0;
    font-size: 12px;
    transition: none;
  }

  // 深色模式下封面分类标签：第一版样式
  .dark .classify {
    background: rgba(30, 30, 30, 0.5);
  }

  // 卡片 hover 效果保留
  &:hover {
    box-shadow: 0 10px 25px -5px rgba(64, 158, 255, 0.1), 0 8px 10px -6px rgba(64, 158, 255, 0.1);
    .dark & {
      box-shadow: 0 10px 25px -5px rgba(64, 158, 255, 0.2), 0 8px 10px -6px rgba(64, 158, 255, 0.15);
    }
  }

  // 文章标签：第一版样式（无额外效果）
  .el-tag {
    transition: all 0.3s ease;
    &:hover {
      transform: none;
      box-shadow: none;
    }
  }
}

// 顶部装饰条动画保留
.absolute.top-0 {
  transition: transform 0.5s cubic-bezier(0.25, 0.1, 0.25, 1);
}

// 文字渐变兼容性保留
.bg-clip-text {
  -webkit-background-clip: text;
}

// 适配移动端保留
@media screen and (max-width: 768px) {
  .card {
    padding: 0.5rem;
  }
}
</style>
