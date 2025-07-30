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
      <el-form-item label="源实体ID" prop="sourceEntityId">
        <el-input
          v-model="queryParams.sourceEntityId"
          placeholder="请输入源实体ID"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="目标实体ID" prop="targetEntityId">
        <el-input
          v-model="queryParams.targetEntityId"
          placeholder="请输入目标实体ID"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="关系类型" prop="relationshipType">
        <el-input
          v-model="queryParams.relationshipType"
          placeholder="请输入关系类型"
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
          v-hasPermi="['ext:relationshipPool:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['ext:relationshipPool:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['ext:relationshipPool:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['ext:relationshipPool:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="relationshipPoolList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" />
      <el-table-column label="工作区ID" align="center" prop="workspaceId" />
      <el-table-column label="任务ID" align="center" prop="taskId" />
      <el-table-column label="文档ID" align="center" prop="docId" />
      <el-table-column label="段落索引" align="center" prop="paragraphIndex" />
      <el-table-column label="源实体ID" align="center" prop="sourceEntityId" />
      <el-table-column label="目标实体ID" align="center" prop="targetEntityId" />
      <el-table-column label="关系类型" align="center" prop="relationshipType" />
      <el-table-column label="处理状态" align="center" prop="status">
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
            v-hasPermi="['ext:relationshipPool:edit']"
          >修改</el-button>
          <el-button
            type="text"
            icon="Delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['ext:relationshipPool:remove']"
          >删除</el-button>
          <el-button
            v-if="scope.row.status === 0"
            type="text"
            icon="Check"
            @click="handleProcess(scope.row, 1)"
            v-hasPermi="['ext:relationshipPool:process']"
          >确认</el-button>
          <el-button
            v-if="scope.row.status === 0"
            type="text"
            icon="Close"
            @click="handleProcess(scope.row, 2)"
            v-hasPermi="['ext:relationshipPool:process']"
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

    <!-- 添加或修改关系池对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="relationshipPoolRef" :model="form" :rules="rules" label-width="80px">
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
        <el-form-item label="源实体ID" prop="sourceEntityId">
          <el-input v-model="form.sourceEntityId" placeholder="请输入源实体ID" />
        </el-form-item>
        <el-form-item label="目标实体ID" prop="targetEntityId">
          <el-input v-model="form.targetEntityId" placeholder="请输入目标实体ID" />
        </el-form-item>
        <el-form-item label="关系类型" prop="relationshipType">
          <el-input v-model="form.relationshipType" placeholder="请输入关系类型" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 处理关系对话框 -->
    <el-dialog title="处理关系" v-model="processOpen" width="500px" append-to-body>
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
  </div>
</template>

<script setup name="RelationshipPool">
import { listRelationshipPool, getRelationshipPool, delRelationshipPool, addRelationshipPool, updateRelationshipPool, processRelationship } from "@/api/ext/extRelationshipPool";

const { proxy } = getCurrentInstance();
const { sys_normal_dict } = proxy.useDict("sys_normal_dict");

const relationshipPoolList = ref([]);
const open = ref(false);
const processOpen = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");

const data = reactive({
  form: {},
  processForm: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    workspaceId: null,
    taskId: null,
    sourceEntityId: null,
    targetEntityId: null,
    relationshipType: null,
    status: null,
    createTime: null,
  },
  rules: {
    workspaceId: [{ required: true, message: "工作区id不能为空", trigger: "blur" }],
    taskId: [{ required: true, message: "任务id不能为空", trigger: "blur" }],
    docId: [{ required: true, message: "文档id不能为空", trigger: "blur" }],
    paragraphIndex: [{ required: true, message: "段落索引不能为空", trigger: "blur" }],
    sourceEntityId: [{ required: true, message: "源实体ID不能为空", trigger: "blur" }],
    targetEntityId: [{ required: true, message: "目标实体ID不能为空", trigger: "blur" }],
    relationshipType: [{ required: true, message: "关系类型不能为空", trigger: "blur" }],
  },
  processRules: {
    status: [{ required: true, message: "处理状态不能为空", trigger: "change" }],
  }
});

const { queryParams, form, processForm, rules, processRules } = toRefs(data);

/** 查询关系池列表 */
function getList() {
  loading.value = true;
  listRelationshipPool(queryParams.value).then(response => {
    // 修复：正确处理后端返回的包装数据结构
    if (response.code === 200 && response.data) {
      relationshipPoolList.value = response.data.rows || response.data.list || [];
      total.value = response.data.total || 0;
    } else {
      relationshipPoolList.value = [];
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

// 表单重置
function reset() {
  form.value = {
    id: null,
    workspaceId: null,
    taskId: null,
    docId: null,
    paragraphIndex: null,
    sourceEntityId: null,
    targetEntityId: null,
    relationshipType: null,
    status: 0,
    validFlag: true,
    delFlag: false,
  };
  proxy.resetForm("relationshipPoolRef");
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
  title.value = "添加关系池";
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  const id = row.id || ids.value;
  getRelationshipPool(id).then(response => {
    if (response.code === 200 && response.data) {
      form.value = response.data;
      open.value = true;
      title.value = "修改关系池";
    } else {
      proxy.$modal.msgError("获取数据失败");
    }
  }).catch(error => {
    console.error('获取关系池详情失败:', error);
    proxy.$modal.msgError("获取数据失败：" + error.message);
  });
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["relationshipPoolRef"].validate(valid => {
    if (valid) {
      if (form.value.id != null) {
        updateRelationshipPool(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addRelationshipPool(form.value).then(response => {
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
  const relationshipPoolIds = row.id || ids.value;
  proxy.$modal.confirm('是否确认删除关系池编号为"' + relationshipPoolIds + '"的数据项？').then(function() {
    return delRelationshipPool(relationshipPoolIds);
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
      processRelationship(processForm.value.id, processForm.value.status, processForm.value.remark).then(response => {
        proxy.$modal.msgSuccess("处理成功");
        processOpen.value = false;
        getList();
      });
    }
  });
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('ext/relationshipPool/export', {
    ...queryParams.value
  }, `relationshipPool_${new Date().getTime()}.xlsx`)
}

getList();
</script> 