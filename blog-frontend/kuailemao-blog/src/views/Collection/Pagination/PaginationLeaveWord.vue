<script setup lang="ts">
import useLeaveWordListStore from "@/store/modules/leaveWordListStore.ts";

const paginationStore = useLeaveWordListStore() ;
</script>

<template>
  <div class="pag">
    <el-pagination background layout="prev, pager, next"
                   :total="paginationStore.leaveWordPagination.total"
                   :hide-on-single-page="true"
                   :page-size="paginationStore.leaveWordPagination.pageSize"
                   @current-change="paginationStore.leaveWordPagination.current = $event"
    />
  </div>
</template>

<style scoped lang="scss">

.pag {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
