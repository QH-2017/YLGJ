<template>
  <div style="display:flex;flex-direction:column;height:calc(100vh - 140px)">
    <!-- 聊天记录 -->
    <div ref="chatBox" style="flex:1;overflow-y:auto;padding-bottom:12px">
      <div v-if="messages.length === 0" style="text-align:center;padding:60px 20px;color:#999">
        <div style="font-size:48px;margin-bottom:12px">🤖</div>
        <p>您好！我是医疗管家AI助手「小医」</p>
        <p style="font-size:13px;margin-top:6px">可以问我关于体检、健康等问题</p>
        <div style="margin-top:20px;display:flex;flex-wrap:wrap;gap:8px;justify-content:center">
          <el-tag v-for="q in quickQuestions" :key="q" style="cursor:pointer" @click="sendMessage(q)">{{ q }}</el-tag>
        </div>
      </div>

      <div v-for="(msg, i) in messages" :key="i" :style="{ display: 'flex', justifyContent: msg.role === 'user' ? 'flex-end' : 'flex-start', marginBottom: '12px' }">
        <div :style="{
          maxWidth: '80%', padding: '10px 14px', borderRadius: msg.role === 'user' ? '12px 12px 4px 12px' : '12px 12px 12px 4px',
          background: msg.role === 'user' ? '#2E8B57' : '#fff', color: msg.role === 'user' ? '#fff' : '#333',
          fontSize: '14px', lineHeight: '1.6', whiteSpace: 'pre-wrap', boxShadow: '0 1px 3px rgba(0,0,0,.08)'
        }">{{ msg.content }}</div>
      </div>

      <div v-if="thinking" style="color:#999;font-size:13px;padding:8px">小医正在思考...</div>
    </div>

    <!-- 输入框 -->
    <div style="display:flex;gap:8px;padding:10px 0;background:#f5f5f5">
      <el-input v-model="inputText" placeholder="输入健康问题..." size="large" @keyup.enter="sendMessage(inputText)" />
      <el-button type="success" size="large" :icon="'Promotion'" circle @click="sendMessage(inputText)" :disabled="!inputText.trim()" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick, watch } from 'vue'
import { aiChat } from '@/api/ai'

const chatBox = ref<HTMLElement>()
const inputText = ref('')
const thinking = ref(false)
const messages = ref<Array<{ role: string; content: string }>>([])

const quickQuestions = ['推荐适合的体检套餐', '血常规检查有什么意义', '如何预约体检', '肿瘤筛查有哪些项目']

async function sendMessage(text: string) {
  const msg = text.trim()
  if (!msg) return
  messages.value.push({ role: 'user', content: msg })
  inputText.value = ''
  thinking.value = true
  await nextTick(); scrollBottom()

  try {
    const res = await aiChat(msg)
    if (res.flag) {
      messages.value.push({ role: 'assistant', content: res.data || '抱歉，我暂时无法回答这个问题。' })
    }
  } catch (e) {
    messages.value.push({ role: 'assistant', content: '抱歉，AI 服务暂时不可用，请稍后再试。' })
  }
  thinking.value = false
  await nextTick(); scrollBottom()
}

function scrollBottom() {
  if (chatBox.value) chatBox.value.scrollTop = chatBox.value.scrollHeight
}
</script>
