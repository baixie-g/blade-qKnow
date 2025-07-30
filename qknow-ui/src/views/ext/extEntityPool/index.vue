<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="工作区" prop="workspaceId">
        <el-input
          v-model="queryParams.workspaceId"
          placeholder="请输入工作区id"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="任务ID" prop="taskId">
        <el-input
          v-model="queryParams.taskId"
          placeholder="请输入任务id"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="实体名称" prop="entityName">
        <el-input
          v-model="queryParams.entityName"
          placeholder="请输入实体名称"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="实体类型" prop="entityType">
        <el-input
          v-model="queryParams.entityType"
          placeholder="请输入实体类型"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="处理状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择处理状态" clearable>
          <el-option label="待处理" value="0" />
          <el-option label="已确认" value="1" />
          <el-option label="已拒绝" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker
          v-model="queryParams.createTime"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="Plus"
          @click="handleAdd"
          v-hasPermi="['ext:entityPool:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['ext:entityPool:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['ext:entityPool:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['ext:entityPool:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="entityPoolList" @selection-change="handleSelectionChange" :key="entityPoolList.length" style="width: 100%" :max-height="600">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" width="80" />
      <el-table-column label="工作区ID" align="center" prop="workspaceId" width="100" />
      <el-table-column label="任务ID" align="center" prop="taskId" width="100" />
      <el-table-column label="文档ID" align="center" prop="docId" width="100" />
      <el-table-column label="段落索引" align="center" prop="paragraphIndex" width="100" />
      <el-table-column label="实体ID" align="center" prop="entityId" width="120" />
      <el-table-column label="实体名称" align="center" prop="entityName" width="120" />
      <el-table-column label="实体类型" align="center" prop="entityType" width="100" />
      <el-table-column label="实体别名" align="center" prop="aliases" width="150" show-overflow-tooltip>
        <template #default="scope">
          <span v-if="scope.row.aliases">{{ JSON.parse(scope.row.aliases).join(', ') }}</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="实体属性" align="center" prop="attributes" width="200" show-overflow-tooltip>
        <template #default="scope">
          <span v-if="scope.row.attributes">{{ JSON.stringify(JSON.parse(scope.row.attributes)) }}</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="处理状态" align="center" prop="status" width="100">
        <template #default="scope">
          <el-tag v-if="scope.row.status === 0" type="warning">待处理</el-tag>
          <el-tag v-else-if="scope.row.status === 1" type="success">已确认</el-tag>
          <el-tag v-else-if="scope.row.status === 2" type="danger">已拒绝</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button
            type="text"
            icon="Edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['ext:entityPool:edit']"
          >修改</el-button>
          <el-button
            type="text"
            icon="Delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['ext:entityPool:remove']"
          >删除</el-button>
          <el-button
            v-if="scope.row.status === 0"
            type="text"
            icon="Search"
            @click="handleDisambiguate(scope.row)"
            v-hasPermi="['ext:entityPool:disambiguate']"
          >消歧</el-button>
          <el-button
            v-if="scope.row.status === 0"
            type="text"
            icon="Check"
            @click="handleProcess(scope.row, 1)"
            v-hasPermi="['ext:entityPool:process']"
          >确认</el-button>
          <el-button
            v-if="scope.row.status === 0"
            type="text"
            icon="Close"
            @click="handleProcess(scope.row, 2)"
            v-hasPermi="['ext:entityPool:process']"
          >拒绝</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改实体池对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="entityPoolRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="工作区ID" prop="workspaceId">
          <el-input v-model="form.workspaceId" placeholder="请输入工作区id" />
        </el-form-item>
        <el-form-item label="任务ID" prop="taskId">
          <el-input v-model="form.taskId" placeholder="请输入任务id" />
        </el-form-item>
        <el-form-item label="文档ID" prop="docId">
          <el-input v-model="form.docId" placeholder="请输入文档id" />
        </el-form-item>
        <el-form-item label="段落索引" prop="paragraphIndex">
          <el-input v-model="form.paragraphIndex" placeholder="请输入段落索引" />
        </el-form-item>
        <el-form-item label="实体ID" prop="entityId">
          <el-input v-model="form.entityId" placeholder="请输入实体ID" />
        </el-form-item>
        <el-form-item label="实体名称" prop="entityName">
          <el-input v-model="form.entityName" placeholder="请输入实体名称" />
        </el-form-item>
        <el-form-item label="实体类型" prop="entityType">
          <el-input v-model="form.entityType" placeholder="请输入实体类型" />
        </el-form-item>
        <el-form-item label="实体别名" prop="aliases">
          <el-input v-model="form.aliases" type="textarea" placeholder="请输入实体别名" />
        </el-form-item>
        <el-form-item label="实体定义" prop="definition">
          <el-input v-model="form.definition" type="textarea" placeholder="请输入实体定义" />
        </el-form-item>
        <el-form-item label="实体属性" prop="attributes">
          <el-input v-model="form.attributes" type="textarea" placeholder="请输入实体属性" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 处理实体对话框 -->
    <el-dialog title="处理实体" v-model="processOpen" width="500px" append-to-body>
      <el-form ref="processRef" :model="processForm" :rules="processRules" label-width="80px">
        <el-form-item label="处理状态" prop="status">
          <el-radio-group v-model="processForm.status">
            <el-radio :label="1">确认</el-radio>
            <el-radio :label="2">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="处理备注" prop="remark">
          <el-input v-model="processForm.remark" type="textarea" placeholder="请输入处理备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitProcess">确 定</el-button>
          <el-button @click="cancelProcess">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 实体消歧对话框 -->
    <el-dialog title="实体消歧" v-model="disambiguationOpen" width="800px" append-to-body>
      <div v-loading="disambiguationLoading">
        <div class="mb-3">
          <h4>待消歧实体：{{ currentEntity.entityName }} ({{ currentEntity.entityType }})</h4>
          <p v-if="currentEntity.definition">定义：{{ currentEntity.definition }}</p>
        </div>

        <div v-if="candidates.length > 0">
          <h5>候选实体列表：</h5>
          <el-table :data="candidates" style="width: 100%">
            <el-table-column label="排名" prop="rank" width="60" align="center" />
            <el-table-column label="实体名称" prop="entity.name" />
            <el-table-column label="实体类型" prop="entity.type" />
            <el-table-column label="定义" prop="entity.definition" show-overflow-tooltip />
            <el-table-column label="相似度" width="120" align="center">
              <template #default="scope">
                <el-progress
                  :percentage="Math.round(scope.row.score.final_score * 100)"
                  :color="getScoreColor(scope.row.score.final_score)"
                />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120" align="center">
              <template #default="scope">
                <el-button
                  type="primary"
                  size="small"
                  @click="handleConfirmCandidate(scope.row)"
                >选择</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <div v-else-if="!disambiguationLoading" class="text-center">
          <el-empty description="未找到匹配的候选实体" />
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancelDisambiguation">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 确认消歧结果对话框 -->
    <el-dialog title="确认消歧结果" v-model="confirmOpen" width="500px" append-to-body>
      <el-form ref="confirmRef" :model="confirmForm" :rules="confirmRules" label-width="80px">
        <el-form-item label="候选实体" prop="candidateId">
          <div class="candidate-info">
            <h4>{{ selectedCandidate.entity.name }} ({{ selectedCandidate.entity.type }})</h4>
            <p v-if="selectedCandidate.entity.definition">定义：{{ selectedCandidate.entity.definition }}</p>
            <p>相似度：{{ Math.round(selectedCandidate.score.final_score * 100) }}%</p>
            <p v-if="selectedCandidate.similarity_details">相似详情：{{ selectedCandidate.similarity_details }}</p>
          </div>
        </el-form-item>
        <el-form-item label="确认备注" prop="remark">
          <el-input v-model="confirmForm.remark" type="textarea" placeholder="请输入确认备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitConfirm">确 定</el-button>
          <el-button @click="cancelConfirm">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="EntityPool">
import { ref, reactive, toRefs, getCurrentInstance, watch } from 'vue';
import { listEntityPool, getEntityPool, delEntityPool, addEntityPool, updateEntityPool, processEntity, disambiguateEntity, confirmDisambiguation } from "@/api/ext/extEntityPool";

const { proxy } = getCurrentInstance();
const { sys_normal_dict } = proxy.useDict("sys_normal_dict");

const entityPoolList = ref([]);
const open = ref(false);
const processOpen = ref(false);
const disambiguationOpen = ref(false);
const confirmOpen = ref(false);
const loading = ref(true);
const disambiguationLoading = ref(false);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");

// 消歧相关数据
const currentEntity = ref({});
const candidates = ref([]);
const selectedCandidate = ref({});

const data = reactive({
  form: {},
  processForm: {},
  confirmForm: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    workspaceId: null,
    taskId: null,
    entityName: null,
    entityType: null,
    status: null,
    createTime: null,
  },
  rules: {
    workspaceId: [{ required: true, message: "工作区id不能为空", trigger: "blur" }],
    taskId: [{ required: true, message: "任务id不能为空", trigger: "blur" }],
    docId: [{ required: true, message: "文档id不能为空", trigger: "blur" }],
    paragraphIndex: [{ required: true, message: "段落索引不能为空", trigger: "blur" }],
    entityId: [{ required: true, message: "实体ID不能为空", trigger: "blur" }],
    entityName: [{ required: true, message: "实体名称不能为空", trigger: "blur" }],
    entityType: [{ required: true, message: "实体类型不能为空", trigger: "blur" }],
  },
  processRules: {
    status: [{ required: true, message: "处理状态不能为空", trigger: "change" }],
  },
  confirmRules: {
    candidateId: [{ required: true, message: "候选实体不能为空", trigger: "change" }],
  }
});

const { queryParams, form, processForm, confirmForm, rules, processRules, confirmRules } = toRefs(data);

/** 查询实体池列表 */
function getList() {
  loading.value = true;
  listEntityPool(queryParams.value).then(response => {
    // 修复：正确处理后端返回的包装数据结构
    if (response.code === 200 && response.data) {
      entityPoolList.value = response.data.rows || response.data.list || [];
      total.value = response.data.total || 0;
    } else {
      entityPoolList.value = [];
      total.value = 0;
    }
    loading.value = false;
  }).catch(error => {
    console.error('API请求失败:', error);
    loading.value = false;
    proxy.$modal.msgError("获取数据失败：" + error.message);
  });
}

// 取消按钮
function cancel() {
  open.value = false;
  reset();
}

// 取消处理
function cancelProcess() {
  processOpen.value = false;
  resetProcess();
}

// 取消消歧
function cancelDisambiguation() {
  disambiguationOpen.value = false;
  candidates.value = [];
  currentEntity.value = {};
}

// 取消确认
function cancelConfirm() {
  confirmOpen.value = false;
  resetConfirm();
}

// 表单重置
function reset() {
  form.value = {
    id: null,
    workspaceId: null,
    taskId: null,
    docId: null,
    paragraphIndex: null,
    entityId: null,
    entityName: null,
    entityType: null,
    aliases: null,
    definition: null,
    attributes: null,
    status: 0,
    validFlag: true,
    delFlag: false,
  };
  proxy.resetForm("entityPoolRef");
}

// 处理表单重置
function resetProcess() {
  processForm.value = {
    id: null,
    status: null,
    remark: null,
  };
  proxy.resetForm("processRef");
}

// 确认表单重置
function resetConfirm() {
  confirmForm.value = {
    entityPoolId: null,
    candidateId: null,
    remark: null,
  };
  proxy.resetForm("confirmRef");
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef");
  handleQuery();
}

// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "添加实体池";
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  const id = row.id || ids.value;
  getEntityPool(id).then(response => {
    form.value = response.data;
    open.value = true;
    title.value = "修改实体池";
  });
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["entityPoolRef"].validate(valid => {
    if (valid) {
      if (form.value.id != null) {
        updateEntityPool(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addEntityPool(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          getList();
        });
      }
    }
  });
}

/** 删除按钮操作 */
function handleDelete(row) {
  const entityPoolIds = row.id || ids.value;
  proxy.$modal.confirm('是否确认删除实体池编号为"' + entityPoolIds + '"的数据项？').then(function() {
    return delEntityPool(entityPoolIds);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {});
}

/** 处理按钮操作 */
function handleProcess(row, status) {
  resetProcess();
  processForm.value.id = row.id;
  processForm.value.status = status;
  processOpen.value = true;
}

/** 提交处理 */
function submitProcess() {
  proxy.$refs["processRef"].validate(valid => {
    if (valid) {
      processEntity(processForm.value.id, processForm.value.status, processForm.value.remark).then(response => {
        proxy.$modal.msgSuccess("处理成功");
        processOpen.value = false;
        getList();
      });
    }
  });
}

/** 消歧按钮操作 */
function handleDisambiguate(row) {
  currentEntity.value = row;
  disambiguationLoading.value = true;
  disambiguationOpen.value = true;

  disambiguateEntity(row.id, 5).then(response => {
    // 兼容两层data结构
    let candidatesArr = [];
    if (
      response.code === 200 &&
      response.data &&
      (
        (response.data.data && Array.isArray(response.data.data.candidates)) ||
        Array.isArray(response.data.candidates)
      )
    ) {
      candidatesArr = response.data.data ? response.data.data.candidates : response.data.candidates;
      candidates.value = candidatesArr;
      if (!candidatesArr.length) {
        proxy.$modal.msgSuccess("该实体无需消歧，无重复实体。");
        disambiguationOpen.value = false;
        return;
      }
    } else {
      candidates.value = [];
      proxy.$modal.msgError("消歧失败：" + ((response.data && response.data.msg) || response.msg || "未知错误"));
      disambiguationOpen.value = false;
      return;
    }
  }).catch(error => {
    proxy.$modal.msgError("消歧请求失败：" + error.message);
    disambiguationOpen.value = false;
  }).finally(() => {
    disambiguationLoading.value = false;
  });
}

/** 选择候选实体 */
function handleConfirmCandidate(candidate) {
  selectedCandidate.value = candidate;
  confirmForm.value.entityPoolId = currentEntity.value.id;
  confirmForm.value.candidateId = candidate.entity.id;
  confirmOpen.value = true;
}

/** 提交确认 */
function submitConfirm() {
  proxy.$refs["confirmRef"].validate(valid => {
    if (valid) {
      confirmDisambiguation(
        confirmForm.value.entityPoolId,
        confirmForm.value.candidateId,
        confirmForm.value.remark
      ).then(response => {
        proxy.$modal.msgSuccess("消歧确认成功");
        confirmOpen.value = false;
        disambiguationOpen.value = false;
        getList();
      });
    }
  });
}

/** 获取相似度颜色 */
function getScoreColor(score) {
  if (score >= 0.8) return '#67C23A';
  if (score >= 0.6) return '#E6A23C';
  return '#F56C6C';
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('ext/entityPool/export', {
    ...queryParams.value
  }, `entityPool_${new Date().getTime()}.xlsx`)
}

getList();
</script>

<style scoped>
.mb-3 {
  margin-bottom: 1rem;
}

.text-center {
  text-align: center;
}

.candidate-info {
  padding: 10px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  background-color: #f5f7fa;
}

.candidate-info h4 {
  margin: 0 0 10px 0;
  color: #303133;
}

.candidate-info p {
  margin: 5px 0;
  color: #606266;
}
</style>

 