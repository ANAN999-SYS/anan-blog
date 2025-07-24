<script setup>
import { defineComponent, h, watch } from "vue";

import { music } from "@/store/modules/music";
import { PLAYTYPE } from "../../musicTool";
import { storeToRefs } from "pinia";
import { ElNotification } from "element-plus";

const { getCustomerMusicList } = storeToRefs(music());

defineComponent({
  name: "CustomMusicList",
});
const playMusic = (item) => {
  // 设置当前播放音乐
  music().setMusicInfo(item.id);
  // 设置播放音乐的详细描述
  music().setPlayType(PLAYTYPE.CUSTOM);
};

const customerDeleteMusic = (item) => {
  music().setCustomerMusicList("delete", item);
  music().setPlayType(PLAYTYPE.CUSTOM);

  ElNotification({
    offset: 60,
    title: "提示",
    duration: 1000,
    message: h("div", { style: "color: #7ec050; font-weight: 600;" }, "删除成功"),
  });
};
const test = () => {
  console.log(getCustomerMusicList.value)
};
watch(
  () => getCustomerMusicList.value.length,
  () => {
    if (!getCustomerMusicList.value.length) {
      music().setPlayType("TOP");
    }
  }
);
</script>

<template>
  <div class="music-list">
    <div class="flex justify-between items-start">
      <div class="!py-[10px] music-list__detail">
        <el-table :data="getCustomerMusicList" empty-text="空空如也" style="width: 100%;margin-top: 12px;" :max-height="240" >
          <el-table-column fixed prop="name" label="歌曲" >
            <template #default="scope">
              {{scope.row.name}}
            </template>
          </el-table-column>
          <el-table-column fixed  label="作者"  >
            <template #default="scope">
              {{scope.row.ar[0].name}}
            </template>
          </el-table-column>
          <el-table-column fixed  label="其他"  >
            <template #default="scope">
              {{scope.row.alia[0]}}
            </template>
          </el-table-column>
          <el-table-column fixed label="操作"  >
            <template #default="scope">
              <el-button
                  size="small"
                  type="success"
                  icon="CaretRight"
                  circle
                  @click="playMusic(scope.row)"
              >
              </el-button>
              <el-button
                  size="small"
                  type="danger"
                  icon="SemiSelect"
                  circle
                  @click="customerDeleteMusic(scope.row)"
              >
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.music-list {
  box-sizing: border-box;
  border-radius: 8px;
  padding: 5px;

  &__detail {
    position: relative;
    overflow: auto;
    width: 100%;
    .header {
      width: 100%;
      display: flex;
      .title {
        font-weight: 600;
        font-size: 1.2rem;
        &1 {
          width: 30%;
        }
        &2 {
          width: 25%;
        }
        &3 {
          width: 25%;
        }
      }
    }

    .body {
      max-height: 300px;
      width: 100%;
      overflow: auto;
    }
  }

  .name {
    width: 30%;
    cursor: pointer;

    &:hover {
      color: var(--music-main-active);
    }
  }

  .author {
    width: 25%;
  }

  .other {
    width: 25%;
  }

  .delete-music {
    &:hover {
      transform: scale(1.1);
    }
  }

  .text-overflow {
    font-size: 1rem;
    display: inline-block;
    width: 100%;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .empty {
    width: 80%;
    height: 25px;
    font-size: 0.8rem;
    display: flex;
    justify-content: center;
    align-items: flex-end;
  }
}
</style>
