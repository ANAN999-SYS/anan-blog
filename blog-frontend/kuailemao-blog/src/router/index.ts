// 使用 vue-router 配置路由
import {createRouter, createWebHistory} from 'vue-router'
import {constantRouter} from '@/router/routers.ts'
import {GET_TOKEN} from "@/utils/auth.ts";
const env = import.meta.env
let router = createRouter({
    // 路由模式 History
    history: createWebHistory(),
    routes: constantRouter
})

router.beforeEach((to, from, next) => {
    // 用户是否登录
    const isLogin = GET_TOKEN()
    // 音乐页面重定向到首页
    if (to.name === 'music'&&!env.VITE_MUSIC_FRONTEND_URL) {
        next('/')
        return
    }

    // 查看文章详情页，滚动条回到顶部
    if (to.name === 'article' || to.name === 'messageDetail') {
        router.afterEach(() => {
            window.scrollTo(0, 0)
        })
    }

    window.document.title = to.meta.title as string

    // 用户登录了，跳转到登录页，直接跳转到首页
    if (to.name?.startsWith('welcome-') && isLogin) {
        next('/')
        return
    }
    next()
})

export default router
