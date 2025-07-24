<template>
  <Main is-side-bar>
    <template #content>
      <div class="title">
        我的收藏
      </div>
      <el-tabs v-model="activeName" class="demo-tabs">
        <el-tab-pane label="文章" name="first"></el-tab-pane>
        <el-tab-pane label="留言" name="second"></el-tab-pane>
      </el-tabs>
      <div v-if="activeName == 'first'">
         <div><ArticalListEssay/></div>
        <div style="margin-top: 1rem">
          <PaginationArtical/>
        </div>
      </div>
      <div v-if="activeName == 'second'">
        <div><LeaveWordList/></div>
        <div style="margin-top: 1rem">
          <PaginationLeaveWord/>
        </div>
      </div>

      <div>
        <el-divider border-style="dashed" content-position="center">
          <div style="font-weight: bold">~~到达底部啦~~</div>
        </el-divider>
      </div>
    </template>
    <!-- 侧边栏 -->
    <template #information>
      <SideBar/>
    </template>
  </Main>
</template>

<script setup lang="ts">
import ArticalListEssay from "@/views/Collection/ArticalListEssay/ArticalListEssay.vue";
import LeaveWordList from "@/views/Collection/LeaveWordList/LeaveWordList.vue";
import PaginationArtical from "@/views/Collection/Pagination/PaginationArtical.vue";
import PaginationLeaveWord from "@/views/Collection/Pagination/PaginationLeaveWord.vue";
const activeName = ref('first');

</script>

<style scoped lang="scss">


.information_container {
  width: 30%;
  height: 100%;
  //border: red 1px solid;
}
.title{
  font-size: 1.5rem;
  padding: 0.5rem 0;
}
.demo-tabs  {
 font-size: 10rem !important;
}
</style>
