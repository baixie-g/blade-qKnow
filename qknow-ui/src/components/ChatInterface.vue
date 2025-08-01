<template>
  <div class="chat-interface">
    <!-- 聊天头部 -->
    <div class="chat-header">
      <div class="header-title">
        <el-icon class="chat-icon"><ChatDotRound /></el-icon>
        <span>智能问答</span>
      </div>
      <div class="header-actions">
        <el-tooltip content="清空聊天记录" placement="bottom">
          <el-button 
            type="text" 
            size="small" 
            @click="clearChat"
            :disabled="chatHistory.length === 0"
          >
            <el-icon><Delete /></el-icon>
          </el-button>
        </el-tooltip>
        <el-tooltip content="设置" placement="bottom">
          <el-button 
            type="text" 
            size="small" 
            @click="showSettings = true"
          >
            <el-icon><Setting /></el-icon>
          </el-button>
        </el-tooltip>
        <el-tooltip content="最小化" placement="bottom">
          <el-button 
            type="text" 
            size="small" 
            @click="$emit('toggle-minimize')"
          >
            <el-icon><Minus /></el-icon>
          </el-button>
        </el-tooltip>
      </div>
    </div>

    <!-- 聊天内容区域 -->
    <div class="chat-content" ref="chatContentRef">
      <div class="chat-messages">
        <div 
          v-for="(message, index) in chatHistory" 
          :key="index"
          :class="['message', message.type]"
        >
          <div class="message-avatar">
            <el-avatar 
              :size="32"
              :src="message.type === 'user' ? userAvatar : botAvatar"
            >
              {{ message.type === 'user' ? '我' : 'AI' }}
            </el-avatar>
          </div>
          <div class="message-content">
            <div class="message-text" v-html="formatMessage(message.content)"></div>
            <div class="message-time">{{ formatTime(message.timestamp) }}</div>
            <!-- Cypher查询结果展示 -->
            <div v-if="message.cypherQuery" class="cypher-section">
              <div class="cypher-header">
                <el-icon><Connection /></el-icon>
                <span>生成的Cypher查询</span>
                <el-button 
                  type="text" 
                  size="small" 
                  @click="copyToClipboard(message.cypherQuery)"
                >
                  <el-icon><CopyDocument /></el-icon>
                </el-button>
              </div>
              <div class="cypher-code">
                <pre>{{ message.cypherQuery }}</pre>
              </div>
            </div>
            <!-- 执行结果展示 -->
            <div v-if="message.executionResult" class="result-section">
              <div class="result-header">
                <el-icon><DataAnalysis /></el-icon>
                <span>查询结果</span>
              </div>
              <div class="result-content">
                <el-table 
                  :data="message.executionResult" 
                  size="small"
                  max-height="200"
                  stripe
                >
                  <el-table-column 
                    v-for="(value, key) in message.executionResult[0] || {}" 
                    :key="key"
                    :prop="key"
                    :label="key"
                    show-overflow-tooltip
                  />
                </el-table>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 加载状态 -->
        <div v-if="isLoading" class="message bot">
          <div class="message-avatar">
            <el-avatar :size="32" :src="botAvatar">AI</el-avatar>
          </div>
          <div class="message-content">
            <div class="loading-dots">
              <span></span>
              <span></span>
              <span></span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 输入区域 -->
    <div class="chat-input">
      <div class="input-container">
        <el-input
          v-model="inputMessage"
          type="textarea"
          :rows="2"
          placeholder="请输入您的问题，例如：查询所有人员信息、查找技术专家等..."
          @keydown.enter.ctrl="sendMessage"
          :disabled="isLoading"
          resize="none"
        />
        <div class="input-actions">
          <el-tooltip content="Ctrl+Enter 发送" placement="top">
            <el-button 
              type="primary" 
              :loading="isLoading"
              @click="sendMessage"
              :disabled="!inputMessage.trim()"
            >
              <el-icon><Promotion /></el-icon>
              发送
            </el-button>
          </el-tooltip>
        </div>
      </div>
    </div>

    <!-- 设置对话框 -->
    <el-dialog 
      v-model="showSettings" 
      title="问答设置" 
      width="500px"
      :append-to-body="true"
    >
      <el-form :model="settings" label-width="120px">
        <el-form-item label="LLM模型">
          <div style="margin-bottom: 8px; font-size: 12px; color: #666;">
            可用模型: {{ availableLLMs.length }} 个
          </div>
          <el-select v-model="settings.llmName" placeholder="选择LLM模型">
            <el-option 
              v-for="llm in availableLLMs" 
              :key="llm.name"
              :label="`${llm.name} (${llm.provider || 'unknown'})`"
              :value="llm.name"
            >
              <div style="display: flex; justify-content: space-between; align-items: center;">
                <span>{{ llm.name }}</span>
                <div style="display: flex; align-items: center; gap: 8px;">
                  <span style="font-size: 12px; color: #666;">{{ llm.provider || 'unknown' }}</span>
                  <el-tag 
                    :type="llm.status === 'available' ? 'success' : 'danger'" 
                    size="small"
                  >
                    {{ llm.status === 'available' ? '可用' : '不可用' }}
                  </el-tag>
                </div>
              </div>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="数据库">
          <el-select v-model="settings.databaseName" placeholder="选择数据库">
            <el-option 
              v-for="db in availableDatabases" 
              :key="db.name"
              :label="db.name"
              :value="db.name"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="工作流类型">
          <el-select v-model="settings.workflowType" placeholder="选择工作流">
            <el-option 
              v-for="workflow in availableWorkflows" 
              :key="workflow.name"
              :label="workflow.description || workflow.name"
              :value="workflow.name"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="超时时间(秒)">
          <el-input-number 
            v-model="settings.timeout" 
            :min="10" 
            :max="300"
            :step="10"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showSettings = false">取消</el-button>
          <el-button type="primary" @click="saveSettings">保存</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  ChatDotRound, 
  Delete, 
  Setting, 
  Minus, 
  Connection, 
  CopyDocument, 
  DataAnalysis, 
  Promotion 
} from '@element-plus/icons-vue'
import { 
  askQuestion, 
  getAvailableLLMs, 
  getAvailableDatabases, 
  getAvailableWorkflows,
  checkText2CypherHealth 
} from '@/api/app/graph/text2cypher'

// 定义事件
const emit = defineEmits(['toggle-minimize'])

// 响应式数据
const chatHistory = ref([])
const inputMessage = ref('')
const isLoading = ref(false)
const showSettings = ref(false)
const chatContentRef = ref(null)

// 设置
const settings = reactive({
  llmName: 'ark-model',
  databaseName: 'neo4j',
  workflowType: 'text2cypher_with_1_retry_and_output_check',
  timeout: 60
})

// 可用选项
const availableLLMs = ref([])
const availableDatabases = ref([])
const availableWorkflows = ref([])

// 头像
const userAvatar = ''
const botAvatar = ''

// 聊天历史存储键
const CHAT_HISTORY_KEY = 'qknow_chat_history'
const CHAT_SETTINGS_KEY = 'qknow_chat_settings'

// 初始化
onMounted(async () => {
  await initializeChat()
})

// 初始化聊天
const initializeChat = async () => {
  try {
    // 加载保存的聊天历史和设置
    loadChatHistory()
    loadSettings()
    
    // 检查LLM服务健康状态
    const healthResponse = await checkText2CypherHealth()
    if (healthResponse.healthy) {
      // 加载可用选项
      await loadAvailableOptions()
      
      // 如果没有聊天历史，添加欢迎消息
      if (chatHistory.value.length === 0) {
        addBotMessage('您好！我是智能问答助手，可以帮助您查询知识图谱。您可以问我关于图谱数据的问题，比如：<br>• 查询所有人员信息<br>• 查找技术专家<br>• 分析实体关系<br>请开始您的提问吧！')
      }
    } else {
      addBotMessage('⚠️ LLM服务暂时不可用，请稍后再试。')
    }
  } catch (error) {
    console.error('初始化聊天失败:', error)
    addBotMessage('⚠️ 无法连接到LLM服务，请检查服务是否正常运行。')
  }
}

// 加载聊天历史
const loadChatHistory = () => {
  try {
    const saved = localStorage.getItem(CHAT_HISTORY_KEY)
    if (saved) {
      const history = JSON.parse(saved)
      // 转换时间戳
      chatHistory.value = history.map(item => ({
        ...item,
        timestamp: new Date(item.timestamp)
      }))
    }
  } catch (error) {
    console.error('加载聊天历史失败:', error)
  }
}

// 保存聊天历史
const saveChatHistory = () => {
  try {
    localStorage.setItem(CHAT_HISTORY_KEY, JSON.stringify(chatHistory.value))
  } catch (error) {
    console.error('保存聊天历史失败:', error)
  }
}

// 加载设置
const loadSettings = () => {
  try {
    const saved = localStorage.getItem(CHAT_SETTINGS_KEY)
    if (saved) {
      const savedSettings = JSON.parse(saved)
      Object.assign(settings, savedSettings)
    }
  } catch (error) {
    console.error('加载设置失败:', error)
  }
}

// 保存设置
const saveSettings = () => {
  try {
    localStorage.setItem(CHAT_SETTINGS_KEY, JSON.stringify(settings))
    showSettings.value = false
    ElMessage.success('设置已保存')
  } catch (error) {
    console.error('保存设置失败:', error)
    ElMessage.error('保存设置失败')
  }
}

// 加载可用选项
const loadAvailableOptions = async () => {
  try {
    const [llmsRes, databasesRes, workflowsRes] = await Promise.all([
      getAvailableLLMs(),
      getAvailableDatabases(),
      getAvailableWorkflows()
    ])
    
    // 修复：后端API直接返回数据，不是包装在success和data字段中
    if (Array.isArray(llmsRes)) {
      availableLLMs.value = llmsRes
    } else if (llmsRes && llmsRes.success && Array.isArray(llmsRes.data)) {
      availableLLMs.value = llmsRes.data
    }
    
    if (Array.isArray(databasesRes)) {
      availableDatabases.value = databasesRes
    } else if (databasesRes && databasesRes.success && Array.isArray(databasesRes.data)) {
      availableDatabases.value = databasesRes.data
    }
    
    if (Array.isArray(workflowsRes)) {
      availableWorkflows.value = workflowsRes
    } else if (workflowsRes && workflowsRes.success && Array.isArray(workflowsRes.data)) {
      availableWorkflows.value = workflowsRes.data
    }
    
    // 设置默认值：如果当前设置的模型不在可用列表中，选择第一个可用模型
    if (availableLLMs.value.length > 0) {
      const currentModelExists = availableLLMs.value.some(llm => llm.name === settings.llmName)
      if (!currentModelExists) {
        settings.llmName = availableLLMs.value[0].name
        console.log('自动选择默认模型:', settings.llmName)
      }
    }
    
    // 设置默认数据库
    if (availableDatabases.value.length > 0) {
      const currentDbExists = availableDatabases.value.some(db => db.name === settings.databaseName)
      if (!currentDbExists) {
        settings.databaseName = availableDatabases.value[0].name
        console.log('自动选择默认数据库:', settings.databaseName)
      }
    }
    
    // 设置默认工作流
    if (availableWorkflows.value.length > 0) {
      const currentWorkflowExists = availableWorkflows.value.some(wf => wf.name === settings.workflowType)
      if (!currentWorkflowExists) {
        settings.workflowType = availableWorkflows.value[0].name
        console.log('自动选择默认工作流:', settings.workflowType)
      }
    }
    
    console.log('加载的可用选项:', {
      llms: availableLLMs.value,
      databases: availableDatabases.value,
      workflows: availableWorkflows.value
    })
  } catch (error) {
    console.error('加载可用选项失败:', error)
  }
}

// 发送消息
const sendMessage = async () => {
  if (!inputMessage.value.trim() || isLoading.value) return
  
  const userMessage = inputMessage.value.trim()
  addUserMessage(userMessage)
  inputMessage.value = ''
  
  isLoading.value = true
  
  try {
    // 构建上下文信息
    const context = {
      graph_context: '当前正在查看知识图谱数据',
      user_query: userMessage,
      timestamp: new Date().toISOString()
    }
    
    const response = await askQuestion(userMessage, context, settings)
    
    console.log('LLM API响应:', response)
    
    if (response.success) {
      let botResponse = ''
      
      // 优先显示answer字段，这是LLM的实际回答
      if (response.answer) {
        botResponse += response.answer
      } else if (response.explanation) {
        botResponse += response.explanation
      } else {
        botResponse += '查询执行成功'
      }
      
      // 显示执行时间
      if (response.executionTime) {
        botResponse += `<br><br><small style="color: #666;">⏱️ 执行时间: ${response.executionTime.toFixed(2)}秒</small>`
      }
      
      addBotMessage(botResponse, {
        cypherQuery: response.cypherQuery,
        executionResult: response.executionResult || null
      })
    } else {
      const errorMsg = response.errorMessage || response.message || '未知错误'
      const errorCode = response.errorCode || 'UNKNOWN_ERROR'
      
      let errorDisplay = `❌ 查询失败：${errorMsg}`
      
      // 根据错误代码提供更友好的错误信息
      switch (errorCode) {
        case 'TIMEOUT_ERROR':
          errorDisplay = '⏰ 查询超时，请尝试简化您的问题或稍后重试'
          break
        case 'CONNECTION_ERROR':
          errorDisplay = '🔌 连接错误，请检查网络连接或联系管理员'
          break
        case 'VALIDATION_ERROR':
          errorDisplay = '📝 输入格式错误，请检查您的问题描述'
          break
        case 'EXECUTION_ERROR':
          errorDisplay = '⚡ 执行错误，请尝试重新描述您的问题'
          break
      }
      
      addBotMessage(errorDisplay)
    }
  } catch (error) {
    console.error('发送消息失败:', error)
    
    let errorMessage = '网络错误'
    if (error.response) {
      const status = error.response.status
      const data = error.response.data
      
      switch (status) {
        case 404:
          errorMessage = '服务未找到，请检查API地址配置'
          break
        case 500:
          errorMessage = '服务器内部错误，请稍后重试'
          break
        case 503:
          errorMessage = '服务暂时不可用，请稍后重试'
          break
        default:
          errorMessage = `服务器错误 (${status}): ${data?.errorMessage || data?.message || '未知错误'}`
      }
    } else if (error.request) {
      errorMessage = '无法连接到LLM服务，请检查服务是否正常运行'
    } else {
      errorMessage = error.message || '未知错误'
    }
    
    addBotMessage(`❌ 抱歉，发生错误：${errorMessage}`)
  } finally {
    isLoading.value = false
  }
}

// 添加用户消息
const addUserMessage = (content) => {
  chatHistory.value.push({
    type: 'user',
    content,
    timestamp: new Date()
  })
  saveChatHistory()
  scrollToBottom()
}

// 添加机器人消息
const addBotMessage = (content, extraData = {}) => {
  chatHistory.value.push({
    type: 'bot',
    content,
    timestamp: new Date(),
    ...extraData
  })
  saveChatHistory()
  scrollToBottom()
}

// 滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    if (chatContentRef.value) {
      chatContentRef.value.scrollTop = chatContentRef.value.scrollHeight
    }
  })
}

// 清空聊天记录
const clearChat = async () => {
  try {
    await ElMessageBox.confirm('确定要清空所有聊天记录吗？此操作不可恢复。', '确认清空', {
      confirmButtonText: '确定清空',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    chatHistory.value = []
    localStorage.removeItem(CHAT_HISTORY_KEY)
    addBotMessage('🗑️ 聊天记录已清空，请开始新的对话。')
  } catch {
    // 用户取消
  }
}

// 复制到剪贴板
const copyToClipboard = (text) => {
  navigator.clipboard.writeText(text).then(() => {
    ElMessage.success('已复制到剪贴板')
  }).catch(() => {
    ElMessage.error('复制失败')
  })
}

// 格式化消息内容
const formatMessage = (content) => {
  return content.replace(/\n/g, '<br>')
}

// 格式化时间
const formatTime = (timestamp) => {
  return new Date(timestamp).toLocaleTimeString('zh-CN', {
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 监听聊天历史变化，自动滚动
watch(chatHistory, () => {
  scrollToBottom()
}, { deep: true })
</script>

<style scoped>
.chat-interface {
  display: flex;
  flex-direction: column;
  height: 100%;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.header-title {
  display: flex;
  align-items: center;
  font-weight: 600;
  font-size: 16px;
}

.chat-icon {
  margin-right: 8px;
  font-size: 18px;
}

.header-actions {
  display: flex;
  gap: 4px;
}

.header-actions .el-button {
  color: white;
}

.header-actions .el-button:hover {
  background: rgba(255, 255, 255, 0.1);
}

.chat-content {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  background: #f8f9fa;
}

.chat-messages {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.message {
  display: flex;
  gap: 12px;
  max-width: 100%;
}

.message.user {
  flex-direction: row-reverse;
}

.message-content {
  max-width: 70%;
}

.message.user .message-content {
  text-align: right;
}

.message-text {
  padding: 12px 16px;
  border-radius: 12px;
  word-wrap: break-word;
  line-height: 1.5;
}

.message.user .message-text {
  background: #007bff;
  color: white;
}

.message.bot .message-text {
  background: white;
  color: #333;
  border: 1px solid #e9ecef;
}

.message-time {
  font-size: 12px;
  color: #6c757d;
  margin-top: 4px;
}

.message.user .message-time {
  text-align: right;
}

.cypher-section, .result-section {
  margin-top: 12px;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  overflow: hidden;
}

.cypher-header, .result-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  background: #f8f9fa;
  border-bottom: 1px solid #e9ecef;
  font-size: 14px;
  font-weight: 500;
}

.cypher-code {
  padding: 12px;
  background: #f8f9fa;
  font-family: 'Courier New', monospace;
  font-size: 13px;
  line-height: 1.4;
  overflow-x: auto;
}

.cypher-code pre {
  margin: 0;
  white-space: pre-wrap;
  word-wrap: break-word;
}

.result-content {
  padding: 12px;
  background: white;
}

.loading-dots {
  display: flex;
  gap: 4px;
  padding: 12px 16px;
}

.loading-dots span {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #007bff;
  animation: loading 1.4s infinite ease-in-out;
}

.loading-dots span:nth-child(1) { animation-delay: -0.32s; }
.loading-dots span:nth-child(2) { animation-delay: -0.16s; }

@keyframes loading {
  0%, 80%, 100% {
    transform: scale(0);
    opacity: 0.5;
  }
  40% {
    transform: scale(1);
    opacity: 1;
  }
}

.chat-input {
  padding: 16px;
  background: white;
  border-top: 1px solid #e9ecef;
}

.input-container {
  display: flex;
  gap: 12px;
  align-items: flex-end;
}

.input-container .el-textarea {
  flex: 1;
}

.input-actions {
  display: flex;
  gap: 8px;
}

/* 滚动条样式 */
.chat-content::-webkit-scrollbar {
  width: 6px;
}

.chat-content::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.chat-content::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.chat-content::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
</style> 