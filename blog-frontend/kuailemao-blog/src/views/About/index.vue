<script setup lang="ts">
import JSConfetti from 'js-confetti'
import useWebsiteStore from "@/store/modules/website.ts";

const jsConfetti = new JSConfetti()

jsConfetti.addConfetti()

const websiteStore = useWebsiteStore()
</script>

<template>
  <div class="flex justify-center items-center h-[100%] w-[99%]" >
    <div class="flex justify-center items-center max-lg:flex-col h-full w-full xl:w-[90%]">
      <div class="w-[40%] max-lg:w-full h-full flex flex-col justify-center items-center">
        <div class="h-[20rem] w-[20rem] rounded-full overflow-hidden mt-[5rem] drop-shadow-lg">
          <div class="bg-cover bg-no-repeat bg-center w-full h-full" :style="{ 'background-image': 'url(' + websiteStore.webInfo?.webmasterAvatar + ')' }"></div>
        </div>
        <div>
          <h1 :data-shadow='websiteStore.webInfo?.webmasterName'>{{ websiteStore.webInfo?.webmasterName }}</h1>
        </div>
        <div class="text-gray-600 w-full font-bold dark:text-gray-300 text-center">
          人生如棋落子无悔道心稳固如箭离弦永不回头
        </div>
      </div>
      <div class="flex flex-col justify-center items-center xl:w-[40%] lg:w-[60%] h-full">
        <div class="w-full h-[60vh] flex flex-col justify-center items-center">
          <div class="w-full text-[3rem] text-center text-gray-600 dark:text-gray-300 max-lg:text-[2rem] max-lg:pb-6">
            一名Web全栈小白
          </div>
          <div class="mt-4 text-center text-gray-500 dark:text-gray-300 max-lg:px-3">
            大家好呀！我是一名刚踏入编程世界的小白，目前正在努力学习 Vue 和 Java 这两门技术～
          </div>
        </div>
        <div class="w-full h-[40vh] flex flex-col justify-center items-center">
          <div class="text-gray-600 dark:text-gray-300 w-64 whitespace-nowrap overflow-hidden" style="width: 300px;text-align: center">
            --------------我的个人导航--------------
          </div>
          <div class="flex justify-center items-center h-full p-4 md:p-8 lg:p-12">
            <a href="https://github.com/ANAN999-SYS" target="_blank">
              <div class="bg-white dark:bg-slate-800 w-[100px] h-[100px] flex justify-center items-center rounded-2xl drop-shadow-lg">
                <SvgIcon name="github_icon" width="80px" height="80px"/>
              </div>
            </a>
            <a href="https://gitee.com/ANAN999-SYS" target="_blank"  class="mx-10 max-lg:mx-4">
              <div class="bg-white dark:bg-slate-800 w-[100px] h-[100px] flex justify-center items-center rounded-2xl drop-shadow-lg">
                <SvgIcon name="gitee_icon" width="80px" height="80px"/>
              </div>
            </a>
            <a href="https://space.bilibili.com/475256259" target="_blank">
              <div class="bg-white dark:bg-slate-800 w-[100px] h-[100px] flex justify-center items-center rounded-2xl drop-shadow-lg">
                <SvgIcon name="bilibili_icon" width="80px" height="80px"/>
              </div>
            </a>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
@import url(https://fonts.googleapis.com/css?family=Righteous);

*, *:before, *:after {
  box-sizing: border-box;
  position: relative;
}

h1 {
  display: inline-block;
  color: white;
  font-family: 'Righteous', serif;
  font-size: 10em;
  text-shadow: .03em .03em 0 hsla(230,40%,50%,1);
  white-space: nowrap;
}
h1:after {
  content: attr(data-shadow);
  position: absolute;
  top: .06em; left: 0.06em;
  z-index: -1;
  text-shadow: none;
  background-image:
      linear-gradient(
              45deg,
              transparent 45%,
              hsla(48,20%,90%,1) 45%,
              hsla(48,20%,90%,1) 55%,
              transparent 0
      );
  background-size: .05em .05em;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;

  animation: shad-anim 15s linear infinite;
}

@keyframes shad-anim {
  0% {background-position: 0 0}
  0% {background-position: 100% -100%}
}
</style>
