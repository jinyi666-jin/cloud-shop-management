<template>
  <!-- 悬浮按钮 -->
  <div 
    class="floating-button" 
    @click="toggleChat"
    :class="{ 'active': isOpen, 'hovered': isHovered }"
    @mouseenter="isHovered = true"
    @mouseleave="isHovered = false"
  >
    <span class="floating-icon">🤖</span>
    <span v-if="unreadCount > 0" class="unread-badge">{{ unreadCount }}</span>
  </div>

  <!-- 悬浮聊天窗口 -->
  <Transition name="slide-up">
    <div v-if="isOpen" class="floating-chat">
      <!-- 窗口头部 -->
      <div class="chat-header">
        <div class="header-left">
          <span class="ai-icon">🤖</span>
        </div>
        <button class="close-btn" @click="toggleChat">✕</button>
      </div>

      <!-- 消息区域 -->
      <div class="chat-messages" ref="messagesContainer">
        <div class="message ai-message">
          <div class="avatar">🤖</div>
          <div class="content">
            <p>您好！我是您的智能购物助手。有什么我可以帮您的吗？</p>
          </div>
        </div>
        
        <div v-for="(msg, index) in messages" :key="index" 
             :class="['message', msg.type]">
          <div class="avatar">{{ msg.type === 'user' ? '👤' : '🤖' }}</div>
          <div class="content">
            <p>{{ msg.content }}</p>
          </div>
        </div>

        <div v-if="loading" class="message ai-message loading">
          <div class="avatar">🤖</div>
          <div class="content">
            <div class="typing-indicator">
              <span></span>
              <span></span>
              <span></span>
            </div>
          </div>
        </div>
      </div>

      <!-- 快捷操作 -->
      <div class="quick-actions">
        <button v-for="action in quickActions" :key="action.label" 
                @click="sendQuickAction(action.message)" 
                class="quick-btn">
          {{ action.label }}
        </button>
      </div>

      <!-- 输入区域 -->
      <div class="chat-input">
        <input 
          v-model="inputMessage" 
          @keyup.enter="sendMessage"
          placeholder="输入您的问题..."
          class="message-input"
          :disabled="loading"
          ref="inputRef"
        />
        <button @click="sendMessage" :disabled="loading || !inputMessage.trim()" class="send-btn">
          <svg class="send-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M22 2L11 13"/>
            <path d="M22 2L15 22L11 13L2 9"/>
          </svg>
        </button>
      </div>
    </div>
  </Transition>
</template>

<script setup>
import { ref, nextTick, onMounted } from 'vue'
import request from '../api/request'

const isOpen = ref(false)
const isHovered = ref(false)
const messages = ref([])
const inputMessage = ref('')
const loading = ref(false)
const unreadCount = ref(0)
const messagesContainer = ref(null)
const inputRef = ref(null)

const quickActions = [
  { label: '推荐商品', message: '帮我推荐一些商品' },
  { label: '查询订单', message: '我的订单状态' },
  { label: '热门商品', message: '热门商品有哪些' },
  { label: '购物指南', message: '如何购买商品' }
]

const toggleChat = () => {
  isOpen.value = !isOpen.value
  if (isOpen.value) {
    unreadCount.value = 0
    nextTick(() => {
      inputRef.value?.focus()
    })
  }
}

const sendMessage = async () => {
  if (!inputMessage.value.trim() || loading.value) return
  
  const message = inputMessage.value.trim()
  inputMessage.value = ''
  
  messages.value.push({ type: 'user', content: message })
  loading.value = true
  
  scrollToBottom()
  
  try {
    const response = await request.post('/ai/chat', {
      message: message,
      userId: localStorage.getItem('userId')
    })
    
    messages.value.push({ type: 'ai', content: response.data.reply })
  } catch (error) {
    messages.value.push({ type: 'ai', content: '抱歉，服务暂时不可用，请稍后重试。' })
  } finally {
    loading.value = false
    scrollToBottom()
  }
}

const sendQuickAction = (message) => {
  inputMessage.value = message
  sendMessage()
}

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

onMounted(() => {
  scrollToBottom()
})
</script>

<style scoped>
/* 悬浮按钮 - 默认露出一半 */
.floating-button {
  position: fixed;
  right: -25px;
  bottom: 50px;
  width: 70px;
  height: 70px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.5);
  z-index: 1000;
  transition: all 0.3s ease;
  border: none;
}

.floating-button:hover,
.floating-button.hovered {
  right: 15px;
  transform: scale(1.05);
  box-shadow: 0 6px 30px rgba(102, 126, 234, 0.6);
}

.floating-button.active {
  right: 15px;
  transform: rotate(45deg);
}

.floating-icon {
  font-size: 28px;
}

.unread-badge {
  position: absolute;
  top: -5px;
  right: -5px;
  background: #ff4757;
  color: white;
  font-size: 12px;
  font-weight: bold;
  padding: 2px 7px;
  border-radius: 10px;
  min-width: 20px;
  text-align: center;
}

/* 悬浮聊天窗口 */
.floating-chat {
  position: fixed;
  right: 30px;
  bottom: 110px;
  width: 380px;
  max-height: 550px;
  background: white;
  border-radius: 20px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  z-index: 999;
  animation: slideUp 0.3s ease;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.slide-up-enter-active,
.slide-up-leave-active {
  transition: all 0.3s ease;
}

.slide-up-enter-from,
.slide-up-leave-to {
  opacity: 0;
  transform: translateY(20px);
}

/* 窗口头部 */
.chat-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 18px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20px 20px 0 0;
  color: white;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.ai-icon {
  font-size: 24px;
}

.close-btn {
  background: rgba(255, 255, 255, 0.2);
  border: none;
  color: white;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  cursor: pointer;
  font-size: 14px;
  transition: background 0.2s;
}

.close-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}

/* 消息区域 */
.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 15px;
  background: #f8f9fa;
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-height: 320px;
}

.message {
  display: flex;
  gap: 10px;
  max-width: 90%;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.message.user {
  align-self: flex-end;
}

.message.ai {
  align-self: flex-start;
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  flex-shrink: 0;
}

.user .avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.ai .avatar {
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
}

.content {
  background: white;
  padding: 10px 14px;
  border-radius: 14px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  max-width: calc(100% - 46px);
}

.user .content {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 14px 14px 4px 14px;
}

.ai .content {
  border-radius: 14px 14px 14px 4px;
}

.content p {
  margin: 0;
  line-height: 1.5;
  font-size: 14px;
  word-break: break-word;
}

/* 输入提示 */
.typing-indicator {
  display: flex;
  gap: 5px;
  padding: 6px 0;
}

.typing-indicator span {
  width: 7px;
  height: 7px;
  background: #999;
  border-radius: 50%;
  animation: typing 1.4s infinite ease-in-out;
}

.typing-indicator span:nth-child(2) { animation-delay: 0.2s; }
.typing-indicator span:nth-child(3) { animation-delay: 0.4s; }

@keyframes typing {
  0%, 80%, 100% { transform: scale(0.6); opacity: 0.5; }
  40% { transform: scale(1); opacity: 1; }
}

/* 快捷操作 */
.quick-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  padding: 12px 15px;
  background: white;
  border-top: 1px solid #eee;
}

.quick-btn {
  padding: 6px 14px;
  background: #f0f2f5;
  border: none;
  border-radius: 16px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s;
  color: #555;
}

.quick-btn:hover {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

/* 输入区域 */
.chat-input {
  display: flex;
  gap: 10px;
  padding: 12px 15px;
  background: white;
  border-top: 1px solid #eee;
  border-radius: 0 0 20px 20px;
}

.message-input {
  flex: 1;
  padding: 10px 14px;
  border: 1px solid #ddd;
  border-radius: 20px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.2s;
}

.message-input:focus {
  border-color: #667eea;
}

.message-input:disabled {
  opacity: 0.6;
}

.send-btn {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.2s, box-shadow 0.2s;
}

.send-btn:hover:not(:disabled) {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.send-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.send-icon {
  width: 18px;
  height: 18px;
}

/* 滚动条样式 */
.chat-messages::-webkit-scrollbar {
  width: 6px;
}

.chat-messages::-webkit-scrollbar-track {
  background: transparent;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: #ddd;
  border-radius: 3px;
}

.chat-messages::-webkit-scrollbar-thumb:hover {
  background: #ccc;
}

/* 响应式 */
@media (max-width: 450px) {
  .floating-button {
    right: -20px;
    bottom: 30px;
    width: 60px;
    height: 60px;
  }
  
  .floating-button:hover,
  .floating-button.hovered,
  .floating-button.active {
    right: 10px;
  }
  
  .floating-chat {
    width: calc(100vw - 40px);
    right: 20px;
    left: 20px;
  }
}
</style>