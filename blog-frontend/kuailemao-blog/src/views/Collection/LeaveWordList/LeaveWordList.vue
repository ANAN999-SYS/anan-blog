<script setup lang="ts">
import {getFavoriteLeaveWordList} from "@/apis/favorite";
import {ElMessage} from "element-plus";
import {useWindowSize} from "@vueuse/core";
import useLeaveWordListStore from "@/store/modules/leaveWordListStore.ts";

const LeaveWordList = ref([]);

const paginationStore = useLeaveWordListStore();

// 监听页数
watch(() => paginationStore.leaveWordPagination.current, () => {
  getLeaveWordListFunc()
  // 滚动到顶部
  window.scrollTo(0, 0);
})
const isGetLeaveWordList = ref(true)
// 屏幕宽度
const { width } = useWindowSize()

function getLeaveWordListFunc() {
  LeaveWordList.value = []
  isGetLeaveWordList.value = true
  getFavoriteLeaveWordList(paginationStore.leaveWordPagination.current, paginationStore.leaveWordPagination.pageSize).then(res => {
    if (res.code === 200) {
      isGetLeaveWordList.value = false;
      paginationStore.leaveWordPagination.total = res.data.total;
      // 过滤内容
      res.data.page = res.data.page.map((item: any) => {
        item.content = item.content.replace(/[*#>`~\-\\[\]()\s]|(\n\n)/g, '')
        // 提取前 50 个字符
        item.content = item.content.substring(0, 60) + '...';
        return item;
      });
      LeaveWordList.value = res.data.page;
    } else {
      ElMessage.error(res.msg);
    }
  })
}
function loadContent() {
  getLeaveWordListFunc()
}
</script>

<template>
  <div class="form" v-view-request="{ callback: loadContent }">
    <template v-for="item in LeaveWordList">
      <el-card class="box-card" shadow="hover" v-slide-in>
        <template #header>
          <div class="card-header">
                <span>
                  <el-avatar :src="item.avatar"/>
                </span>
            <span class="name">{{ item.nickname }}</span>
            <span class="time">{{ item.createTime }}</span>
          </div>
        </template>
        <div class="text">
          {{ item.content }}
        </div>
        <div class="bottom">
          <div class="count">
            <div>
              <SvgIcon name="comments"/>
              <span>{{ item.commentCount }}</span>
            </div>
            <div>
              <SvgIcon name="like"/>
              <span>{{ item.likeCount }}</span>
            </div>
            <div>
              <SvgIcon name="collection"/>
              <span>{{ item.favoriteCount }}</span>
            </div>
          </div>
          <div>
            <el-link type="primary" @click="$router.push(`/message/detail/${item.id}`)">
              <template #icon>
                <el-icon>
                  <ArrowRightBold/>
                </el-icon>
              </template>
              查看详情
            </el-link>
          </div>
        </div>
      </el-card>
    </template>
  </div>
  <template v-if="LeaveWordList.length == 0&&isGetLeaveWordList">
    <el-skeleton :rows="8" animated />
  </template>
  <template v-if="!isGetLeaveWordList&&LeaveWordList.length == 0">
    <div style="display:flex;justify-content:center;align-items:center;height:88px">
      <div>此处啥也没有~~</div>
    </div>
  </template>
</template>

<style scoped lang="scss">
.box-card {
  margin-top: 1rem;
  font-size: 0.9rem;

  .bottom {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: 1rem;
    color: grey;

    .count {
      display: flex;
      margin-top: 0.5rem;

      div {
        display: flex;
        align-items: center;
        margin-right: 1rem;
      }

      span {
        margin-left: 0.2rem;
      }
    }
  }

  .text {
    margin-bottom: 1rem;

    .reply {
      margin-top: 3rem;

    }
  }

  .card-header {
    display: flex;
    align-items: center;

    .name {
      margin-left: 0.5rem;
      font-size: 1.3rem;
    }

    .time {
      // 右对齐
      flex: 1;
      text-align: right;
      margin-left: 1rem;
      font-size: 0.85rem;
      color: #999;
    }
  }
}
</style>
