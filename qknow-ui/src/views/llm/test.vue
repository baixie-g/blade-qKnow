<template>
  <div class="app-container">
    <el-card>
      <div slot="header">
        <span>提示词管理功能测试</span>
      </div>
      
      <el-row :gutter="20">
        <el-col :span="12">
          <el-card>
            <div slot="header">
              <span>API测试</span>
            </div>
            
            <el-button @click="testGetTypes" type="primary">测试获取提示词类型</el-button>
            <el-button @click="testGetTemplates" type="success">测试获取模板列表</el-button>
            <el-button @click="testGetFiles" type="warning">测试获取文件信息</el-button>
            
            <div v-if="testResult" style="margin-top: 20px;">
              <h4>测试结果：</h4>
              <pre>{{ JSON.stringify(testResult, null, 2) }}</pre>
            </div>
          </el-card>
        </el-col>
        
        <el-col :span="12">
          <el-card>
            <div slot="header">
              <span>连接状态</span>
            </div>
            
            <el-descriptions :column="1" border>
              <el-descriptions-item label="8003服务状态">
                <el-tag :type="serviceStatus.healthy ? 'success' : 'danger'">
                  {{ serviceStatus.healthy ? '正常' : '异常' }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="服务地址">
                {{ serviceStatus.baseUrl }}
              </el-descriptions-item>
              <el-descriptions-item label="最后检查时间">
                {{ serviceStatus.lastCheck }}
              </el-descriptions-item>
            </el-descriptions>
            
            <el-button @click="checkServiceStatus" style="margin-top: 20px;" type="info">
              检查服务状态
            </el-button>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script>
import { 
  getPromptTypes, 
  getTemplates, 
  getTemplateFiles 
} from "@/api/llm/prompts";

export default {
  name: "LLMTest",
  data() {
    return {
      testResult: null,
      serviceStatus: {
        healthy: false,
        baseUrl: 'http://localhost:8003',
        lastCheck: '-'
      }
    };
  },
  created() {
    this.checkServiceStatus();
  },
  methods: {
    async testGetTypes() {
      try {
        this.testResult = null;
        const response = await getPromptTypes();
        this.testResult = response;
        this.$message.success('获取提示词类型成功');
      } catch (error) {
        this.$message.error('获取提示词类型失败: ' + error.message);
        this.testResult = { error: error.message };
      }
    },
    
    async testGetTemplates() {
      try {
        this.testResult = null;
        const response = await getTemplates({ page: 1, pageSize: 5 });
        this.testResult = response;
        this.$message.success('获取模板列表成功');
      } catch (error) {
        this.$message.error('获取模板列表失败: ' + error.message);
        this.testResult = { error: error.message };
      }
    },
    
    async testGetFiles() {
      try {
        this.testResult = null;
        const response = await getTemplateFiles();
        this.testResult = response;
        this.$message.success('获取文件信息成功');
      } catch (error) {
        this.$message.error('获取文件信息失败: ' + error.message);
        this.testResult = { error: error.message };
      }
    },
    
    async checkServiceStatus() {
      try {
        const response = await getPromptTypes();
        this.serviceStatus.healthy = response.success;
        this.serviceStatus.lastCheck = new Date().toLocaleString();
        if (response.success) {
          this.$message.success('8003服务连接正常');
        } else {
          this.$message.warning('8003服务响应异常');
        }
      } catch (error) {
        this.serviceStatus.healthy = false;
        this.serviceStatus.lastCheck = new Date().toLocaleString();
        this.$message.error('8003服务连接失败: ' + error.message);
      }
    }
  }
};
</script>

<style scoped>
pre {
  background-color: #f5f5f5;
  padding: 10px;
  border-radius: 4px;
  max-height: 300px;
  overflow-y: auto;
}
</style>
