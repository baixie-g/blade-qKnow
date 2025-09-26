<template>
  <div class="app-container">
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-refresh"
          size="small"
          @click="handleRefresh"
        >刷新</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="typeList">
      <el-table-column label="类型标识" align="center" prop="type" :show-overflow-tooltip="true" />
      <el-table-column label="类型名称" align="center" prop="name" :show-overflow-tooltip="true" />
      <el-table-column label="类型描述" align="center" prop="description" :show-overflow-tooltip="true" />
      <el-table-column label="所属工作流" align="center" prop="workflow" :show-overflow-tooltip="true" />
      <el-table-column label="所属步骤" align="center" prop="step" :show-overflow-tooltip="true" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleViewTemplates(scope.row)"
          >查看模板</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 查看类型模板对话框 -->
    <el-dialog title="查看提示词类型模板" :visible.sync="viewOpen" width="1000px" append-to-body>
      <el-form :inline="true" class="mb8">
        <el-form-item>
          <el-input
            v-model="templateQuery.search"
            placeholder="请输入模板名称"
            clearable
            size="small"
            @keyup.enter.native="handleTemplateQuery"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="small" @click="handleTemplateQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" size="small" @click="resetTemplateQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="templateLoading" :data="templateList">
        <el-table-column label="模板名称" align="center" prop="name" :show-overflow-tooltip="true" />
        <el-table-column label="描述" align="center" prop="description" :show-overflow-tooltip="true" />
        <el-table-column label="版本" align="center" prop="version" width="80" />
        <el-table-column label="默认" align="center" prop="isDefault" width="80">
          <template #default="{ row }">
            <el-tag :type="row.isDefault ? 'success' : 'info'">
              {{ row.isDefault ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" align="center" prop="isActive" width="80">
          <template #default="{ row }">
            <el-tag :type="row.isActive ? 'success' : 'danger'">
              {{ row.isActive ? '激活' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" align="center" prop="createdAt" width="180">
          <template #default="{ row }">
            <span>{{ parseTime(row.createdAt) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="{ row }">
            <el-button
              size="small"
              type="text"
              icon="el-icon-view"
              @click="handleViewTemplate(row)"
            >查看</el-button>
            <el-button
              size="small"
              type="text"
              icon="el-icon-edit"
              @click="handleUpdateTemplate(row)"
            >修改</el-button>
            <el-button
              size="small"
              type="text"
              icon="el-icon-copy-document"
              @click="handleCopyTemplate(row)"
            >复制</el-button>
            <el-button
              size="small"
              type="text"
              icon="el-icon-delete"
              @click="handleDeleteTemplate(row)"
              v-if="!row.isDefault"
            >删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <pagination
        v-show="templateTotal>0"
        :total="templateTotal"
        :page.sync="templateQuery.page"
        :limit.sync="templateQuery.pageSize"
        @pagination="getTemplateList"
      />

      <!-- 查看模板详情对话框 -->
      <el-dialog title="查看模板详情" :visible.sync="templateViewOpen" width="800px" append-to-body>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="模板名称">{{ templateViewForm.name }}</el-descriptions-item>
          <el-descriptions-item label="提示词类型">{{ templateViewForm.promptType }}</el-descriptions-item>
          <el-descriptions-item label="版本">{{ templateViewForm.version }}</el-descriptions-item>
          <el-descriptions-item label="是否默认">
            <el-tag :type="templateViewForm.isDefault ? 'success' : 'info'">
              {{ templateViewForm.isDefault ? '是' : '否' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="是否激活">
            <el-tag :type="templateViewForm.isActive ? 'success' : 'danger'">
              {{ templateViewForm.isActive ? '激活' : '禁用' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ parseTime(templateViewForm.createdAt) }}</el-descriptions-item>
          <el-descriptions-item label="更新时间">{{ parseTime(templateViewForm.updatedAt) }}</el-descriptions-item>
          <el-descriptions-item label="模板描述" :span="2">{{ templateViewForm.description }}</el-descriptions-item>
          <el-descriptions-item label="提示词内容" :span="2">
            <el-input
              v-model="templateViewForm.content"
              type="textarea"
              :rows="8"
              readonly
            />
          </el-descriptions-item>
        </el-descriptions>
      </el-dialog>

      <!-- 修改模板对话框 -->
      <el-dialog title="修改模板" :visible.sync="templateEditOpen" width="800px" append-to-body>
        <el-form ref="templateForm" :model="templateForm" :rules="templateRules" label-width="120px">
          <el-row>
            <el-col :span="12">
              <el-form-item label="模板名称" prop="name">
                <el-input v-model="templateForm.name" placeholder="请输入模板名称" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="提示词类型" prop="promptType">
                <el-input v-model="templateForm.promptType" disabled />
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="模板描述" prop="description">
            <el-input v-model="templateForm.description" type="textarea" placeholder="请输入模板描述" />
          </el-form-item>
          <el-form-item label="提示词内容" prop="content">
            <el-input
              v-model="templateForm.content"
              type="textarea"
              :rows="8"
              placeholder="请输入提示词内容"
            />
          </el-form-item>
          <el-row>
            <el-col :span="12">
              <el-form-item label="是否默认" prop="isDefault">
                <el-radio-group v-model="templateForm.isDefault">
                  <el-radio :label="true">是</el-radio>
                  <el-radio :label="false">否</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="是否激活" prop="isActive">
                <el-radio-group v-model="templateForm.isActive">
                  <el-radio :label="true">激活</el-radio>
                  <el-radio :label="false">禁用</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="submitTemplateForm">确 定</el-button>
          <el-button @click="cancelTemplateEdit">取 消</el-button>
        </div>
      </el-dialog>

      <!-- 复制模板对话框 -->
      <el-dialog title="复制模板" :visible.sync="templateCopyOpen" width="500px" append-to-body>
        <el-form ref="templateCopyForm" :model="templateCopyForm" :rules="templateCopyRules" label-width="120px">
          <el-form-item label="新模板名称" prop="newName">
            <el-input v-model="templateCopyForm.newName" placeholder="请输入新模板名称" />
          </el-form-item>
          <el-form-item label="新模板描述" prop="description">
            <el-input v-model="templateCopyForm.description" type="textarea" placeholder="请输入新模板描述" />
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="submitTemplateCopyForm">确 定</el-button>
          <el-button @click="cancelTemplateCopy">取 消</el-button>
        </div>
      </el-dialog>
    </el-dialog>
  </div>
</template>

<script>
import { 
  getPromptTypes, 
  getTemplatesByType,
  getTemplateDetail,
  updateTemplate,
  deleteTemplate,
  copyTemplate
} from "@/api/llm/prompts";

export default {
  name: "PromptTypes",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 显示搜索条件
      showSearch: true,
      // 提示词类型列表数据
      typeList: [],
      // 是否显示查看模板对话框
      viewOpen: false,
      // 当前查看的类型
      currentType: null,
      // 模板列表数据
      templateList: [],
      // 模板加载状态
      templateLoading: false,
      // 模板总数
      templateTotal: 0,
      // 模板查询参数
      templateQuery: {
        page: 1,
        pageSize: 20,
        search: undefined
      },
      // 是否显示模板查看对话框
      templateViewOpen: false,
      // 是否显示模板编辑对话框
      templateEditOpen: false,
      // 是否显示模板复制对话框
      templateCopyOpen: false,
      // 模板查看表单
      templateViewForm: {},
      // 模板编辑表单
      templateForm: {},
      // 模板复制表单
      templateCopyForm: {},
      // 模板表单校验
      templateRules: {
        name: [
          { required: true, message: "模板名称不能为空", trigger: "blur" }
        ],
        content: [
          { required: true, message: "提示词内容不能为空", trigger: "blur" }
        ]
      },
      // 模板复制表单校验
      templateCopyRules: {
        newName: [
          { required: true, message: "新模板名称不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询提示词类型列表 */
    getList() {
      this.loading = true;
      getPromptTypes().then(response => {
        if (response.success) {
          this.typeList = response.data;
        }
        this.loading = false;
      });
    },
    /** 刷新按钮操作 */
    handleRefresh() {
      this.getList();
    },
    /** 查看类型模板 */
    handleViewTemplates(row) {
      this.currentType = row;
      this.viewOpen = true;
      this.templateQuery.search = undefined;
      this.getTemplateList();
    },
    /** 查询模板列表 */
    getTemplateList() {
      this.templateLoading = true;
      getTemplatesByType(this.currentType.type, this.templateQuery).then(response => {
        if (response.success) {
          this.templateList = response.data.templates;
          this.templateTotal = response.data.total;
        }
        this.templateLoading = false;
      });
    },
    /** 搜索模板 */
    handleTemplateQuery() {
      this.templateQuery.page = 1;
      this.getTemplateList();
    },
    /** 重置模板查询 */
    resetTemplateQuery() {
      this.templateQuery.search = undefined;
      this.handleTemplateQuery();
    },
    /** 查看模板详情 */
    handleViewTemplate(row) {
      getTemplateDetail(row.id).then(response => {
        if (response.success) {
          this.templateViewForm = response.data;
          this.templateViewOpen = true;
        }
      });
    },
    /** 修改模板 */
    handleUpdateTemplate(row) {
      getTemplateDetail(row.id).then(response => {
        if (response.success) {
          this.templateForm = { ...response.data };
          this.templateEditOpen = true;
        }
      });
    },
    /** 复制模板 */
    handleCopyTemplate(row) {
      this.templateCopyForm = {
        newName: row.name + "_副本",
        description: row.description
      };
      this.templateCopyOpen = true;
    },
    /** 删除模板 */
    handleDeleteTemplate(row) {
      this.$modal.confirm('是否确认删除提示词模板编号为"' + row.id + '"的数据项？').then(function() {
        return deleteTemplate(row.id);
      }).then(() => {
        this.getTemplateList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 提交模板表单 */
    submitTemplateForm() {
      this.$refs["templateForm"].validate(valid => {
        if (valid) {
          updateTemplate(this.templateForm.id, this.templateForm).then(response => {
            if (response.success) {
              this.$modal.msgSuccess("修改成功");
              this.templateEditOpen = false;
              this.getTemplateList();
            }
          });
        }
      });
    },
    /** 提交模板复制表单 */
    submitTemplateCopyForm() {
      this.$refs["templateCopyForm"].validate(valid => {
        if (valid) {
          copyTemplate(this.templateForm.id, this.templateCopyForm).then(response => {
            if (response.success) {
              this.$modal.msgSuccess("复制成功");
              this.templateCopyOpen = false;
              this.getTemplateList();
            }
          });
        }
      });
    },
    /** 取消模板编辑 */
    cancelTemplateEdit() {
      this.templateEditOpen = false;
      this.templateForm = {};
    },
    /** 取消模板复制 */
    cancelTemplateCopy() {
      this.templateCopyOpen = false;
      this.templateCopyForm = {};
    }
  }
};
</script>
