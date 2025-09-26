<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="模板名称" prop="search">
        <el-input
          v-model="queryParams.search"
          placeholder="请输入模板名称"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="提示词类型" prop="promptType">
        <el-select v-model="queryParams.promptType" placeholder="请选择提示词类型" clearable size="small">
          <el-option
            v-for="type in promptTypes"
            :key="type.type"
            :label="type.name"
            :value="type.type"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="isActive">
        <el-select v-model="queryParams.isActive" placeholder="请选择状态" clearable size="small">
          <el-option label="激活" :value="true" />
          <el-option label="禁用" :value="false" />
        </el-select>
      </el-form-item>
      <el-form-item label="默认" prop="isDefault">
        <el-select v-model="queryParams.isDefault" placeholder="请选择默认状态" clearable size="small">
          <el-option label="是" :value="true" />
          <el-option label="否" :value="false" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="small" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="small" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="small"
          @click="handleAdd"
          v-hasPermi="['llm:prompts:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-upload"
          size="small"
          @click="handleImport"
          v-hasPermi="['llm:prompts:import']"
        >导入</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="small"
          @click="handleExport"
          v-hasPermi="['llm:prompts:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="templateList || []" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="模板名称" align="center" prop="name" :show-overflow-tooltip="true" />
      <el-table-column label="提示词类型" align="center" prop="promptType" :show-overflow-tooltip="true">
        <template #default="{ row }">
          {{ row && row.promptType ? getPromptTypeName(row.promptType) : '' }}
        </template>
      </el-table-column>
      <el-table-column label="描述" align="center" prop="description" :show-overflow-tooltip="true" />
      <el-table-column label="版本" align="center" prop="version" width="80" />
      <el-table-column label="默认" align="center" prop="isDefault" width="80">
        <template #default="{ row }">
          <el-tag v-if="row" :type="row.isDefault ? 'success' : 'info'">
            {{ row.isDefault ? '是' : '否' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="isActive" width="80">
        <template #default="{ row }">
          <el-tag v-if="row" :type="row.isActive ? 'success' : 'danger'">
            {{ row.isActive ? '激活' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createdAt" width="180">
        <template #default="{ row }">
          <span v-if="row">{{ parseTime(row.createdAt) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="{ row }">
          <el-button
            v-if="row"
            size="small"
            type="text"
            icon="el-icon-view"
            @click="handleView(row)"
            v-hasPermi="['llm:prompts:query']"
          >查看</el-button>
          <el-button
            v-if="row"
            size="small"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(row)"
            v-hasPermi="['llm:prompts:edit']"
          >修改</el-button>
          <el-button
            v-if="row"
            size="small"
            type="text"
            icon="el-icon-copy-document"
            @click="handleCopy(row)"
            v-hasPermi="['llm:prompts:add']"
          >复制</el-button>
          <el-button
            v-if="row && !row.isDefault"
            size="small"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(row)"
            v-hasPermi="['llm:prompts:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.page"
      :limit.sync="queryParams.pageSize"
      @pagination="handlePagination"
    />

    <!-- 添加或修改提示词模板对话框 -->
    <el-dialog :title="title" v-model="open" width="800px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="模板名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入模板名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="提示词类型" prop="promptType">
              <el-select v-model="form.promptType" placeholder="请选择提示词类型" style="width: 100%">
                <el-option
                  v-for="type in promptTypes"
                  :key="type.type"
                  :label="type.name"
                  :value="type.type"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="模板描述" prop="description">
          <el-input v-model="form.description" type="textarea" placeholder="请输入模板描述" />
        </el-form-item>
        <el-form-item label="提示词内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="8"
            placeholder="请输入提示词内容"
          />
        </el-form-item>
        <el-row>
          <el-col :span="12">
            <el-form-item label="是否默认" prop="isDefault">
              <el-radio-group v-model="form.isDefault">
                <el-radio :label="true">是</el-radio>
                <el-radio :label="false">否</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="是否激活" prop="isActive">
              <el-radio-group v-model="form.isActive">
                <el-radio :label="true">激活</el-radio>
                <el-radio :label="false">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 查看提示词模板对话框 -->
    <el-dialog title="查看提示词模板" v-model="viewOpen" width="800px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="模板名称">{{ viewForm.name }}</el-descriptions-item>
        <el-descriptions-item label="提示词类型">{{ getPromptTypeName(viewForm.promptType) }}</el-descriptions-item>
        <el-descriptions-item label="版本">{{ viewForm.version }}</el-descriptions-item>
        <el-descriptions-item label="是否默认">
          <el-tag :type="viewForm.isDefault ? 'success' : 'info'">
            {{ viewForm.isDefault ? '是' : '否' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="是否激活">
          <el-tag :type="viewForm.isActive ? 'success' : 'danger'">
            {{ viewForm.isActive ? '激活' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ parseTime(viewForm.createdAt) }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ parseTime(viewForm.updatedAt) }}</el-descriptions-item>
        <el-descriptions-item label="模板描述" :span="2">{{ viewForm.description }}</el-descriptions-item>
        <el-descriptions-item label="提示词内容" :span="2">
          <el-input
            v-model="viewForm.content"
            type="textarea"
            :rows="8"
            readonly
          />
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 复制模板对话框 -->
    <el-dialog title="复制模板" v-model="copyOpen" width="500px" append-to-body>
      <el-form ref="copyForm" :model="copyForm" :rules="copyRules" label-width="120px">
        <el-form-item label="新模板名称" prop="newName">
          <el-input v-model="copyForm.newName" placeholder="请输入新模板名称" />
        </el-form-item>
        <el-form-item label="新模板描述" prop="description">
          <el-input v-model="copyForm.description" type="textarea" placeholder="请输入新模板描述" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitCopyForm">确 定</el-button>
        <el-button @click="cancelCopy">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 导入对话框 -->
    <el-dialog title="批量导入模板" v-model="importOpen" width="400px" append-to-body>
      <el-upload
        ref="upload"
        :limit="1"
        accept=".json"
        :headers="upload.headers"
        :action="upload.url"
        :disabled="upload.isUploading"
        :on-progress="handleFileUploadProgress"
        :on-success="handleFileSuccess"
        :auto-upload="false"
        drag
      >
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <div class="el-upload__tip text-center" slot="tip">
          <div class="el-upload__tip">只能上传json文件</div>
        </div>
      </el-upload>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitFileForm">确 定</el-button>
        <el-button @click="importOpen = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { 
  getTemplates, 
  getTemplateDetail, 
  createTemplate, 
  updateTemplate, 
  deleteTemplate, 
  copyTemplate,
  getPromptTypes,
  exportTemplates,
  importTemplates
} from "@/api/llm/prompts";

export default {
  name: "PromptTemplates",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 提示词模板表格数据
      templateList: [],
      // 提示词类型数据
      promptTypes: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 是否显示查看弹出层
      viewOpen: false,
      // 是否显示复制弹出层
      copyOpen: false,
      // 是否显示导入弹出层
      importOpen: false,
      // 查询参数
      queryParams: {
        page: 1,
        pageSize: 20,
        search: undefined,
        promptType: undefined,
        isActive: undefined,
        isDefault: undefined
      },
      // 表单参数
      form: {},
      // 查看表单参数
      viewForm: {},
      // 复制表单参数
      copyForm: {},
      // 当前复制的模板ID
      currentTemplateId: null,
      // 表单校验
      rules: {
        name: [
          { required: true, message: "模板名称不能为空", trigger: "blur" }
        ],
        promptType: [
          { required: true, message: "提示词类型不能为空", trigger: "change" }
        ],
        content: [
          { required: true, message: "提示词内容不能为空", trigger: "blur" }
        ]
      },
      // 复制表单校验
      copyRules: {
        newName: [
          { required: true, message: "新模板名称不能为空", trigger: "blur" }
        ]
      },
      // 上传参数
      upload: {
        // 是否禁用上传
        isUploading: false,
        // 设置上传的请求头部
        headers: {},
        // 上传的地址 - 使用相对路径，让Vite代理处理
        url: "/api/llm/prompts/templates/import"
      }
    };
  },
  created() {
    // 先获取提示词类型，再获取模板列表
    this.getPromptTypes().then(() => {
      this.getList();
    });
  },
  methods: {
    /** 查询提示词模板列表 */
    getList() {
      this.loading = true;
      console.log('调用getList，查询参数:', this.queryParams);
      getTemplates(this.queryParams).then(response => {
        console.log('模板列表响应:', response);
        // 根据实际API响应，这个接口返回 {templates: [...], total: ..., page: ..., pageSize: ..., totalPages: ...}
        if (response && response.templates) {
          this.templateList = response.templates || [];
          this.total = response.total || 0;
        } else {
          this.templateList = [];
          this.total = 0;
        }
        this.loading = false;
      }).catch(error => {
        console.error('获取模板列表失败:', error);
        this.templateList = [];
        this.total = 0;
        this.loading = false;
      });
    },
    /** 查询提示词类型列表 */
    getPromptTypes() {
      return getPromptTypes().then(response => {
        console.log('提示词类型响应:', response);
        // 根据实际API响应，这个接口返回 {success: true, message: "...", data: [...]}
        if (response && response.success && response.data) {
          this.promptTypes = response.data;
        } else {
          this.promptTypes = [];
        }
        return this.promptTypes;
      }).catch(error => {
        console.error('获取提示词类型失败:', error);
        this.promptTypes = [];
        return this.promptTypes;
      });
    },
    /** 分页处理 */
    handlePagination(val) {
      console.log('分页事件触发:', val);
      this.queryParams.page = val.page;
      this.queryParams.pageSize = val.limit;
      this.getList();
    },
    /** 搜索按钮操作 */
    handleQuery() {
      console.log('搜索按钮点击，查询参数:', this.queryParams);
      this.queryParams.page = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      console.log('重置按钮点击');
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加提示词模板";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      console.log('修改按钮点击，行数据:', row);
      this.reset();
      const templateId = row.id || this.ids;
      console.log('模板ID:', templateId);
      getTemplateDetail(templateId).then(response => {
        console.log('获取模板详情响应:', response);
        if (response.success) {
          console.log('设置表单数据:', response.data);
          this.form = response.data;
          console.log('设置open为true');
          this.open = true;
          this.title = "修改提示词模板";
          console.log('当前open值:', this.open);
          console.log('当前form值:', this.form);
        } else {
          console.error('获取模板详情失败:', response);
        }
      }).catch(error => {
        console.error('获取模板详情异常:', error);
      });
    },
    /** 查看按钮操作 */
    handleView(row) {
      console.log('查看按钮点击，行数据:', row);
      this.reset();
      const templateId = row.id || this.ids;
      console.log('模板ID:', templateId);
      getTemplateDetail(templateId).then(response => {
        console.log('获取模板详情响应:', response);
        if (response.success) {
          console.log('设置查看表单数据:', response.data);
          this.viewForm = response.data;
          console.log('设置viewOpen为true');
          this.viewOpen = true;
          console.log('当前viewOpen值:', this.viewOpen);
          console.log('当前viewForm值:', this.viewForm);
        } else {
          console.error('获取模板详情失败:', response);
        }
      }).catch(error => {
        console.error('获取模板详情异常:', error);
      });
    },
    /** 复制按钮操作 */
    handleCopy(row) {
      this.copyForm = {
        newName: row.name + "_副本",
        description: row.description
      };
      this.currentTemplateId = row.id;
      this.copyOpen = true;
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateTemplate(this.form.id, this.form).then(response => {
              if (response.success) {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              }
            });
          } else {
            createTemplate(this.form).then(response => {
              if (response.success) {
                this.$modal.msgSuccess("新增成功");
                this.open = false;
                this.getList();
              }
            });
          }
        }
      });
    },
    /** 提交复制表单 */
    submitCopyForm() {
      this.$refs["copyForm"].validate(valid => {
        if (valid) {
          copyTemplate(this.currentTemplateId, this.copyForm).then(response => {
            if (response.success) {
              this.$modal.msgSuccess("复制成功");
              this.copyOpen = false;
              this.getList();
            }
          });
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const templateIds = row.id || this.ids;
      this.$modal.confirm('是否确认删除提示词模板编号为"' + templateIds + '"的数据项？').then(function() {
        return deleteTemplate(templateIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导入按钮操作 */
    handleImport() {
      // 动态设置上传headers
      this.upload.headers = { Authorization: "Bearer " + this.$store.getters.token };
      this.importOpen = true;
    },
    /** 导出按钮操作 */
    handleExport() {
      this.$modal.confirm('是否确认导出所有提示词模板数据项？').then(() => {
        this.exportLoading = true;
        exportTemplates().then(response => {
          if (response.success) {
            this.download(response.data, '提示词模板数据', 'json');
          }
          this.exportLoading = false;
        });
      });
    },
    // 文件上传中处理
    handleFileUploadProgress(event, file, fileList) {
      this.upload.isUploading = true;
    },
    // 文件上传成功处理
    handleFileSuccess(response, file, fileList) {
      this.upload.isUploading = false;
      this.$refs.upload.clearFiles();
      this.$alert(response.msg, "导入结果", { dangerouslyUseHTMLString: true });
      this.importOpen = false;
      this.getList();
    },
    // 提交上传文件
    submitFileForm() {
      this.$refs.upload.submit();
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 取消复制按钮
    cancelCopy() {
      this.copyOpen = false;
      this.copyForm = {};
      this.currentTemplateId = null;
    },
    // 表单重置
    reset() {
      this.form = {
        name: undefined,
        promptType: undefined,
        content: undefined,
        description: undefined,
        isDefault: false,
        isActive: true
      };
      this.resetForm("form");
    },
    // 获取提示词类型名称
    getPromptTypeName(type) {
      console.log('getPromptTypeName called with type:', type);
      console.log('this.promptTypes:', this.promptTypes);
      
      if (!type || !this.promptTypes || !Array.isArray(this.promptTypes)) {
        console.log('Returning type directly:', type);
        return type || '';
      }
      
      const promptType = this.promptTypes.find(item => item && item.type === type);
      console.log('Found promptType:', promptType);
      return promptType ? promptType.name : type;
    }
  }
};
</script>
