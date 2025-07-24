import http from "@/utils/http.ts";
import EasyTyper from "easy-typer-js";
// 第三方的api接口
// 一言接口
let myYiYan = import.meta.env.VITE_YIYAN_API
if (!myYiYan) {
    myYiYan = 'https://v1.hitokoto.cn/?c=a&encode=json'
}
// 每日鸡汤
export const getSoup = () => {
    return http({
        url: myYiYan,
        method: 'get'
    })
}

// 整合API获取与打字机逻辑的函数（带闭包状态管理）
const getSoupTyping = (() => {
    let isTyping = false; // 状态锁：防止重复执行
    let currentInstance: any = null; // 当前打字机实例
    let currentTimer: number | null = null; // 当前定时器ID

    // 主逻辑函数
    const main = async (
        obj: any,
        saveInstance: (instance: any) => void,
        saveTimer: (timerId: number) => void
    ) => {
        console.log("[打字机] 尝试启动流程，当前状态:", isTyping, new Date().toLocaleTimeString());

        // 防止重复调用
        if (isTyping) {
            console.log("[打字机] 已在执行中，跳过本次调用");
            return;
        }

        try {
            isTyping = true; // 锁定状态

            // 1. 调用API获取打字内容
            console.log("[打字机] 开始请求API获取文本");
            const res = await fetch(myYiYan);

            if (!res.ok) {
                throw new Error(`API请求失败: ${res.status} ${res.statusText}`);
            }

            const data = await res.json();
            let text = data?.message;

            // 处理API返回异常
            if (!text || typeof text !== "string") {
                console.warn("[打字机] API返回文本无效，使用默认文本");
                text = "生活就像海洋，只有意志坚强的人才能到达彼岸。\n—— 马克思";
            }

            console.log("[打字机] 获取到文本:", text);

            // 2. 销毁可能存在的旧实例
            if (currentInstance && currentInstance.destroy) {
                currentInstance.destroy();
                console.log("[打字机] 已销毁旧实例");
            }

            // 3. 创建新的打字机实例
            console.log("[打字机] 开始创建EasyTyper实例");
            currentInstance = new EasyTyper(
                obj,
                text, // 使用API获取的文本（支持\n换行）
                () => {
                    // 打字完成回调
                    console.log("[打字机] 打字动画完成", new Date().toLocaleTimeString());
                    isTyping = false;

                    // 计划下一轮（保存定时器ID）
                    currentTimer = setTimeout(() => {
                        console.log("[打字机] 定时器触发，开始下一轮");
                        main(obj, saveInstance, saveTimer);
                    }, 25000); // 间隔25秒后继续

                    // 同步定时器ID到组件
                    saveTimer(currentTimer);
                    console.log("[打字机] 已设置下一轮定时器，ID:", currentTimer);
                },
                () => {
                    // 打字开始回调
                    console.log("[打字机] 打字动画开始");
                    saveInstance(currentInstance); // 同步实例到组件
                }
            );

        } catch (error) {
            // 错误处理：解锁状态，避免永久锁定
            isTyping = false;
            console.error("[打字机] 流程出错:", error);

            // 出错后仍尝试下一轮（可选）
            currentTimer = setTimeout(() => {
                console.log("[打字机] 错误后重试下一轮");
                main(obj, saveInstance, saveTimer);
            }, 10000); // 错误后延长间隔至10秒
            saveTimer(currentTimer);
        }
    };

    // 重置方法：组件销毁时调用
    (main as any).reset = () => {
        console.log("[打字机] 执行重置操作");
        isTyping = false;

        // 清除定时器
        if (currentTimer) {
            clearTimeout(currentTimer);
            console.log("[打字机] 已清除定时器，ID:", currentTimer);
            currentTimer = null;
        }

        // 销毁实例
        if (currentInstance && currentInstance.destroy) {
            currentInstance.destroy();
            console.log("[打字机] 已销毁当前实例");
            currentInstance = null;
        }

        console.log("[打字机] 重置完成");
    };

    return main;
})();

export default getSoupTyping; // 导出函数
