<script setup lang="ts">
import {Watermelon} from '@element-plus/icons-vue'
import {ElMessage, ElNotification, FormInstance, FormRules} from "element-plus";
import {applyLink, linkList} from "@/apis/link";
import {MdPreview} from "md-editor-v3";

const dialogVisible = ref(false)

onMounted(() => {
  linkListFunc()
})

const links = ref()

function linkListFunc() {
  linkList().then(res => {
    if (res.code === 200) {
      links.value = res.data
    } else {
      ElMessage.error(res.msg)
    }
  })
}

const form = reactive({
  name: '',
  url: '',
  description: '',
  background: '',
  email: '',
  type: '1'
})

const ruleFormRef = ref<FormInstance>()

const rules = reactive<FormRules<any>>({
  name: [
    {required: true, message: '请填写网站名称', trigger: 'blur'},
    {min: 3, max: 15, message: '长度小3，最大15', trigger: 'blur'},
  ],
  url: [
    {required: true, message: '请填写网站地址', trigger: 'blur'},
    {min: 3, max: 50, message: '长度小3，最大50', trigger: 'blur'},
  ],
  description: [
    {required: true, message: '请填写网站描述', trigger: 'blur'},
    {min: 3, max: 30, message: '长度小3，最大15', trigger: 'blur'},
  ],
  background: [
    {required: true, message: '请填写友链背景图链接', trigger: 'blur'},
    {min: 3, max: 100, message: '长度小3，最大100', trigger: 'blur'},
  ],
  email: [
    {required: true, message: '请填写电子邮件地址', trigger: 'blur'},
    {min: 3, max: 20, message: '长度小3，最大15', trigger: 'blur'},
  ]
})

// 申请链接
function applyLinkFunc() {
  ruleFormRef.value?.validate(async (valid) => {
    if (valid) {
      await applyLink(form).then(res => {
        if (res.code === 200) {
          ElNotification({
            title: '成功',
            showClose: false,
            duration: 6000,
            message: '友链申请提交成功，待通过审核后会通过邮件通知您，请注意查收',
            type: 'success',
          })
          dialogVisible.value = false
        } else {
          ElMessage.error(res.msg)
        }
      })
    } else {
      return false
    }
  })
}

</script>

<template>
  <div>
    <el-dialog
        v-model="dialogVisible"
        title="申请友链"
        width="35%"
        style="border-radius: 15px"
        :close-on-click-modal="false"
        :lock-scroll="false"
    >
      <div class="form">
        <div style="display: flex;align-items: center;margin-bottom: 1rem">
        </div>
        <el-form :model="form" ref="ruleFormRef" :rules="rules">
          <el-form-item prop="name">
            <el-input v-model="form.name" placeholder="请输入网站名称" maxlength="15" show-word-limit>
              <template #prepend>
                网站名称
              </template>
            </el-input>
          </el-form-item>
          <el-form-item prop="url">
            <el-input v-model="form.url" placeholder="请输入网站地址" maxlength="50" show-word-limit>
              <template #prepend>
                网站地址
              </template>
            </el-input>
          </el-form-item>
          <el-form-item prop="description">
            <el-input v-model="form.description" placeholder="请输入网站描述" maxlength="30" show-word-limit>
              <template #prepend>
                网站描述
              </template>
            </el-input>
          </el-form-item>
          <el-form-item prop="background">
            <el-input v-model="form.background" placeholder="请提供http地址" maxlength="100" show-word-limit>
              <template #prepend>
                背景图片
              </template>
            </el-input>
          </el-form-item>
          <el-form-item prop="email">
            <el-input v-model="form.email" placeholder="填写邮箱地址" maxlength="20" show-word-limit>
              <template #prepend>
                邮箱地址
              </template>
            </el-input>
          </el-form-item>
          <div style="width: 100%;display: flex;flex-direction: row-reverse">
            <el-button type="primary" plain @click="applyLinkFunc">提交申请</el-button>
          </div>
        </el-form>
      </div>
    </el-dialog>
    <Main only-father-container>
      <template #banner>
        <Banner title="友链" subtitle="link"/>
      </template>
      <template #content>
        <div class="content">
          <div class="header">
            <div class="title">友链</div>
            <el-button type="primary" :icon="Watermelon" plain @click="dialogVisible = true" style="margin-right: 1rem">申请友链</el-button>
          </div>
          <el-divider/>
          <div class="title_content">
            <span style="font-size: 1rem;color: grey">欢迎访问友链板块！</span>
            <span>友链板块是一个旨在促进不同系统间相互协作和交流的平台。通过友链板块，您可以：</span>
            <span>1、分享自己系统的介绍和链接。</span>
            <span>2、发现更多的优秀博客网站。</span>
            <span style="font-size: 1rem;color: grey">注意：</span>
            <span>1、友链申请前必须先登录本网站，申请通过后会通过邮件的形式通知你。</span>
            <span>2、点击网站的名称进行友链跳转。</span>
          </div>
          <div class="link">
            <template v-for="link in links" :key="link.id">
              <div v-slide-in class="item group">
                <!-- 背景图容器，添加渐变叠加增强文字可读性 -->
                <div class="bg" :style="{backgroundImage: `url(${link.background})`}">
                  <div class="bg-overlay"></div>
                </div>

                <div class="content_link">
                  <div class="info-card">
                    <!-- 头像添加过渡效果 -->
                    <div class="avatar-container">
                      <el-avatar class="avatar" :src="link.avatar" />
                    </div>

                    <div class="text-content">
                      <div class="name">
                        <a :href="link.url" class="name-link" target="_blank" rel="noopener noreferrer">
                          {{ link.name }}
                        </a>
                      </div>
                      <div class="description line-clamp-2">{{ link.description }}</div>
                    </div>
                  </div>
                </div>
              </div>
            </template>
          </div>
        </div>
      </template>
    </Main>
  </div>
</template>

<style scoped lang="scss">
@import "@/styles/mixin.scss";

// 全局Dialog样式优化
:deep(.el-dialog__body) {
  padding-top: 0;
}

:deep(.el-dialog) {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);

  @media (max-width: 1000px) {
    width: 60%;
  }

  @media (max-width: 550px) {
    width: 90%;
  }
}

.content {
  margin-top: 1.5rem;

  .link {
    display: flex;
    flex-wrap: wrap;
    gap: 1rem; // 使用gap替代margin实现更均匀的间距
    padding: 0.5rem; // 容器内边距，防止边缘卡片贴边
  }

  .item {
    flex: 1;
    min-width: 280px; // 确保小屏幕上也有合理的最小宽度
    max-width: calc(100% / 3 - 1rem);
    height: 13rem;
    border: 1px solid #0072ff;
    border-radius: $border-radius;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    @include flex;
    flex-direction: column;
    overflow: hidden;
    transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
    position: relative;

    // 响应式断点优化
    @media screen and (max-width: 1024px) {
      max-width: calc(100% / 2 - 1rem);
    }

    @media screen and (max-width: 640px) {
      max-width: 100%;
      height: 12rem;
    }

    // 悬停效果增强
    &:hover {
      transform: translateY(-5px) scale(1.01);
      box-shadow: 0 12px 30px rgba(0, 114, 255, 0.15);
      border-color: rgba(0, 114, 255, 0.8);

      .content_link {
        height: 50%;
        background: #0072ff;

        .name {
          color: #fdeeee;
        }

        .description {
          color: rgba(253, 238, 238, 0.9);
        }
      }

      .bg {
        filter: blur(3px) brightness(0.85);
        transform: scale(1.05);
      }

      .avatar {
        transform: scale(1.08);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
      }
    }

    .bg {
      background-size: cover !important;
      background-position: center !important;
      width: 100%;
      height: 65%;
      transition: all 0.6s cubic-bezier(0.4, 0, 0.2, 1);
      position: relative;

      // 添加渐变叠加层，提升文字可读性
      .bg-overlay {
        position: absolute;
        inset: 0;
        background: linear-gradient(to bottom,
            rgba(0,0,0,0) 0%,
            rgba(0,0,0,0.2) 100%);
      }
    }

    .content_link {
      transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
      display: flex;
      align-items: center;
      justify-content: center;
      height: 35%;
      width: 100%;
      padding: 0 1rem;
      position: relative;
      z-index: 1; // 确保内容在背景之上
    }

    .info-card {
      display: flex;
      align-items: center;
      width: fit-content;
      max-width: 300px; // 限制最大宽度，防止在宽屏上过度拉伸
      gap: 0.8rem; // 使用gap替代margin，更现代的布局方式
    }

    .avatar-container {
      flex-shrink: 0; // 防止头像被压缩
    }

    .avatar {
      width: 3.5rem;
      height: 3.5rem;
      border: 2px solid white;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      transition: all 0.3s ease;
    }

    .text-content {
      flex: 1;
      min-width: 0; // 解决flex子元素内容溢出问题
    }

    .name {
      font-size: 1rem;
      font-weight: 600;
      transition: color 0.3s ease;

      .name-link {
        color: inherit;
        text-decoration: none;
        cursor: pointer;
        transition: all 0.2s ease;

        &:hover {
          text-decoration: underline;
          text-underline-offset: 2px;
        }
      }
    }

    .description {
      line-height: 1.4;
      font-size: 0.85rem;
      margin-top: 0.25rem;
      color: #7C7C7C;
      transition: color 0.3s ease;
      max-width: 100%;
    }
  }

  .title_content {
    font-weight: 600;
    font-size: 0.8rem;
    color: #999;
    display: flex;
    flex-direction: column;
    background: var(--mao-bg-message);
    padding: 0.75rem;
    border-radius: $border-radius;
    margin-bottom: 1rem;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);

    span {
      margin-bottom: 1rem;
      line-height: 1.5;
    }
  }

  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 1.5rem;

    .el-button {
      height: 2.5rem;
      padding: 0 1.25rem;
      transition: all 0.2s ease;
    }

    .title {
      font-size: 2rem;
      font-weight: 700;
      color: #333;
    }
  }
}

// 工具类：文本超出两行显示省略号
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

</style>
