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
          type="success"
          plain
          icon="Check"
          :disabled="multiple"
          @click="handleBatchConfirm"
          v-hasPermi="['ext:entityPool:process']"
        >批量确认</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Close"
          :disabled="multiple"
          @click="handleBatchReject"
          v-hasPermi="['ext:entityPool:process']"
        >批量拒绝</el-button>
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
          <span v-if="scope.row.aliases">
            <span v-if="scope.row.aliases.trim().startsWith('[') && scope.row.aliases.trim().endsWith(']')">
              {{ JSON.parse(scope.row.aliases).join(', ') }}
            </span>
            <span v-else>
              {{ scope.row.aliases }}
            </span>
          </span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="实体属性" align="center" prop="attributes" width="200" show-overflow-tooltip>
        <template #default="scope">
          <span v-if="scope.row.attributes">
            <span v-if="scope.row.attributes.trim().startsWith('{') && scope.row.attributes.trim().endsWith('}')">
              {{ JSON.stringify(JSON.parse(scope.row.attributes)) }}
            </span>
            <span v-else>
              {{ scope.row.attributes }}
            </span>
          </span>
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
    <el-dialog :title="title" v-model="open" width="800px" append-to-body>
      <el-form ref="entityPoolRef" :model="form" :rules="rules" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="工作区ID" prop="workspaceId">
              <el-input v-model.number="form.workspaceId" placeholder="请输入工作区ID（数字）" type="number" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="任务ID" prop="taskId">
              <el-input v-model.number="form.taskId" placeholder="请输入任务ID（数字）" type="number" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="文档ID" prop="docId">
              <el-input v-model.number="form.docId" placeholder="请输入文档ID（数字）" type="number" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="段落索引" prop="paragraphIndex">
              <el-input v-model.number="form.paragraphIndex" placeholder="请输入段落索引（数字）" type="number" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="实体ID" prop="entityId">
              <el-input v-model="form.entityId" placeholder="请输入实体ID（如：person_001）" />
              <div class="form-tip">格式：类型_编号，如：person_001, company_001</div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="实体名称" prop="entityName">
              <el-input v-model="form.entityName" placeholder="请输入实体名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="实体类型" prop="entityType">
              <el-select v-model="form.entityType" placeholder="请选择实体类型" style="width: 100%">
                <el-option label="人物" value="人物" />
                <el-option label="组织" value="组织" />
                <el-option label="地点" value="地点" />
                <el-option label="技术" value="技术" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="实体别名" prop="aliases">
              <el-input v-model="form.aliases" placeholder="请输入实体别名，多个用逗号分隔" />
              <div class="form-tip">格式：别名1,别名2,别名3 或 JSON数组格式：["别名1","别名2"]</div>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="实体定义" prop="definition">
              <el-input v-model="form.definition" type="textarea" :rows="3" placeholder="请输入实体定义描述" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="实体属性" prop="attributes">
              <el-input v-model="form.attributes" type="textarea" :rows="4" placeholder="请输入实体属性（JSON格式）" />
              <div class="form-tip">
                格式：{"属性名":["值1","值2"],"属性名2":["值3"]}<br>
                示例：{"毕业院校":["清华大学"],"工作单位":["字节跳动"],"职位":["研究员"]}
              </div>
            </el-form-item>
          </el-col>
        </el-row>
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
            <el-table-column label="操作" align="center" width="200">
              <template #default="scope">
                <el-button
                  size="small"
                  type="primary"
                  @click="handleViewCandidateDetails(scope.row)"
                >
                  查看详情
                </el-button>
                <el-button
                  size="small"
                  type="success"
                  @click="handleConfirmCandidate(scope.row)"
                >
                  选择此实体
                </el-button>
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

    <!-- 实体对比和合并对话框 -->
    <el-dialog title="实体信息对比与合并" v-model="compareDialogVisible" width="1400px" append-to-body>
      <div v-if="currentEntityForCompare && candidateEntity">
        <el-row :gutter="20">
          <!-- 当前实体信息 -->
          <el-col :span="12">
            <el-card header="当前实体信息" shadow="hover">
              <el-descriptions :column="1" border>
                <el-descriptions-item label="实体ID">{{ currentEntityForCompare.entityId }}</el-descriptions-item>
                <el-descriptions-item label="实体名称">{{ currentEntityForCompare.entityName }}</el-descriptions-item>
                <el-descriptions-item label="实体类型">{{ currentEntityForCompare.entityType }}</el-descriptions-item>
                <el-descriptions-item label="实体别名">
                  <div v-if="currentEntityForCompare.aliases">
                    <span v-if="Array.isArray(currentEntityForCompare.aliases)">
                      {{ currentEntityForCompare.aliases.join(', ') }}
                    </span>
                    <span v-else>{{ currentEntityForCompare.aliases }}</span>
                  </div>
                  <span v-else>-</span>
                </el-descriptions-item>
                <el-descriptions-item label="实体定义">
                  {{ currentEntityForCompare.definition || '-' }}
                </el-descriptions-item>
                <el-descriptions-item label="实体属性">
                  <div v-if="currentEntityForCompare.attributes">
                    <pre v-if="typeof currentEntityForCompare.attributes === 'object'">{{ JSON.stringify(currentEntityForCompare.attributes, null, 2) }}</pre>
                    <span v-else>{{ currentEntityForCompare.attributes }}</span>
                  </div>
                  <span v-else>-</span>
                </el-descriptions-item>
              </el-descriptions>
            </el-card>
          </el-col>
          
          <!-- 候选实体信息 -->
          <el-col :span="12">
            <el-card header="候选实体信息" shadow="hover">
              <template #header>
                <div style="display: flex; justify-content: space-between; align-items: center;">
                  <span>候选实体信息</span>
                  <el-button size="small" type="info" @click="showCandidateDebugInfo">
                    调试信息
                  </el-button>
                </div>
              </template>
              <el-descriptions :column="1" border>
                <el-descriptions-item label="实体ID">{{ candidateEntity.id }}</el-descriptions-item>
                <el-descriptions-item label="实体名称">{{ candidateEntity.name }}</el-descriptions-item>
                <el-descriptions-item label="实体类型">{{ candidateEntity.type }}</el-descriptions-item>
                <el-descriptions-item label="实体别名">
                  <div v-if="getCandidateAliases(candidateEntity)">
                    <span v-if="Array.isArray(getCandidateAliases(candidateEntity))">
                      {{ getCandidateAliases(candidateEntity).join(', ') }}
                    </span>
                    <span v-else-if="typeof getCandidateAliases(candidateEntity) === 'string'">
                      <span v-if="getCandidateAliases(candidateEntity).trim().startsWith('[') && getCandidateAliases(candidateEntity).trim().endsWith(']')">
                        {{ JSON.parse(getCandidateAliases(candidateEntity)).join(', ') }}
                      </span>
                      <span v-else>
                        {{ getCandidateAliases(candidateEntity) }}
                      </span>
                    </span>
                    <span v-else>
                      {{ JSON.stringify(getCandidateAliases(candidateEntity)) }}
                    </span>
                  </div>
                  <span v-else>-</span>
                </el-descriptions-item>
                <el-descriptions-item label="实体定义">
                  <div v-if="getCandidateDefinition(candidateEntity)">
                    {{ getCandidateDefinition(candidateEntity) }}
                  </div>
                  <span v-else>-</span>
                </el-descriptions-item>
                <el-descriptions-item label="实体属性">
                  <div v-if="getCandidateAttributes(candidateEntity)">
                    <pre v-if="typeof getCandidateAttributes(candidateEntity) === 'object'">{{ JSON.stringify(getCandidateAttributes(candidateEntity), null, 2) }}</pre>
                    <span v-else-if="typeof getCandidateAttributes(candidateEntity) === 'string'">
                      <span v-if="getCandidateAttributes(candidateEntity).trim().startsWith('{') && getCandidateAttributes(candidateEntity).trim().endsWith('}')">
                        <pre>{{ JSON.stringify(JSON.parse(getCandidateAttributes(candidateEntity)), null, 2) }}</pre>
                      </span>
                      <span v-else>
                        {{ getCandidateAttributes(candidateEntity) }}
                      </span>
                    </span>
                    <span v-else>
                      {{ JSON.stringify(getCandidateAttributes(candidateEntity)) }}
                    </span>
                  </div>
                  <span v-else>-</span>
                </el-descriptions-item>
                <!-- 显示其他重要属性 -->
                <el-descriptions-item v-if="candidateEntity.dynamicProperties && candidateEntity.dynamicProperties.workspace_id" label="工作区ID">
                  {{ candidateEntity.dynamicProperties.workspace_id }}
                </el-descriptions-item>
                <el-descriptions-item v-if="candidateEntity.dynamicProperties && candidateEntity.dynamicProperties.task_id" label="任务ID">
                  {{ candidateEntity.dynamicProperties.task_id }}
                </el-descriptions-item>
                <el-descriptions-item v-if="candidateEntity.dynamicProperties && candidateEntity.dynamicProperties.doc_id" label="文档ID">
                  {{ candidateEntity.dynamicProperties.doc_id }}
                </el-descriptions-item>
                <el-descriptions-item v-if="candidateEntity.dynamicProperties && candidateEntity.dynamicProperties.paragraph_index" label="段落索引">
                  {{ candidateEntity.dynamicProperties.paragraph_index }}
                </el-descriptions-item>
              </el-descriptions>
            </el-card>
          </el-col>
        </el-row>
        
        <!-- 合并策略选择 -->
        <el-card header="合并策略" style="margin-top: 20px;" shadow="hover">
          <el-form :model="mergeForm" label-width="120px">
            <el-form-item label="合并策略">
              <el-radio-group v-model="mergeForm.mergeStrategy" @change="handleMergeStrategyChange">
                <el-radio label="replace">替换策略：用当前实体的信息替换候选实体的对应字段</el-radio>
                <el-radio label="append">追加策略：将当前实体的信息追加到候选实体的对应字段</el-radio>
                <el-radio label="merge">智能合并：自动合并两个实体的信息，避免重复</el-radio>
                <el-radio label="manual">手动合并：手动编辑合并结果（类似GitHub合并冲突）</el-radio>
              </el-radio-group>
            </el-form-item>
            
            <el-form-item label="合并备注">
              <el-input v-model="mergeForm.remark" type="textarea" :rows="3" placeholder="请输入合并备注信息" />
            </el-form-item>
          </el-form>
        </el-card>
        
        <!-- 手动合并界面 -->
        <el-card v-if="mergeForm.mergeStrategy === 'manual'" header="手动合并编辑" style="margin-top: 20px;" shadow="hover">
          <div class="merge-tip" style="margin-bottom: 15px; padding: 10px; background-color: #f0f9ff; border: 1px solid #b3d8ff; border-radius: 4px;">
            <el-icon><InfoFilled /></el-icon>
            <span style="margin-left: 5px; color: #409eff;">
              以下输入框已自动填充智能合并的默认值，您可以根据需要进行调整。
            </span>
            <el-button size="small" type="primary" style="margin-left: 10px;" @click="resetToSmartMerge">
              重置为智能合并
            </el-button>
          </div>
          <div class="merge-editor">
            <el-row :gutter="20">
              <el-col :span="8">
                <div class="merge-section">
                  <h4>当前实体信息</h4>
                  <div class="merge-content">
                    <div class="merge-field">
                      <label>实体名称：</label>
                      <div class="field-value">{{ currentEntityForCompare.entityName || '-' }}</div>
                    </div>
                    <div class="merge-field">
                      <label>实体别名：</label>
                      <div class="field-value">{{ formatFieldValue(currentEntityForCompare.aliases) }}</div>
                    </div>
                    <div class="merge-field">
                      <label>实体定义：</label>
                      <div class="field-value">{{ currentEntityForCompare.definition || '-' }}</div>
                    </div>
                    <div class="merge-field">
                      <label>实体属性：</label>
                      <div class="field-value">{{ formatFieldValue(currentEntityForCompare.attributes) }}</div>
                    </div>
                  </div>
                </div>
              </el-col>
              
              <el-col :span="8">
                <div class="merge-section">
                  <h4>候选实体信息</h4>
                  <div class="merge-content">
                    <div class="merge-field">
                      <label>实体名称：</label>
                      <div class="field-value">{{ candidateEntity.name || '-' }}</div>
                    </div>
                    <div class="merge-field">
                      <label>实体别名：</label>
                      <div class="field-value">
                        <span v-if="getCandidateAliases(candidateEntity)">
                          <span v-if="Array.isArray(getCandidateAliases(candidateEntity))">
                            {{ getCandidateAliases(candidateEntity).join(', ') }}
                          </span>
                          <span v-else-if="typeof getCandidateAliases(candidateEntity) === 'string'">
                            <span v-if="getCandidateAliases(candidateEntity).trim().startsWith('[') && getCandidateAliases(candidateEntity).trim().endsWith(']')">
                              {{ JSON.parse(getCandidateAliases(candidateEntity)).join(', ') }}
                            </span>
                            <span v-else>
                              {{ getCandidateAliases(candidateEntity) }}
                            </span>
                          </span>
                          <span v-else>
                            {{ JSON.stringify(getCandidateAliases(candidateEntity)) }}
                          </span>
                        </span>
                        <span v-else>-</span>
                      </div>
                    </div>
                    <div class="merge-field">
                      <label>实体定义：</label>
                      <div class="field-value">{{ getCandidateDefinition(candidateEntity) || '-' }}</div>
                    </div>
                    <div class="merge-field">
                      <label>实体属性：</label>
                      <div class="field-value">
                        <span v-if="getCandidateAttributes(candidateEntity)">
                          <span v-if="typeof getCandidateAttributes(candidateEntity) === 'object'">
                            {{ JSON.stringify(getCandidateAttributes(candidateEntity), null, 2) }}
                          </span>
                          <span v-else-if="typeof getCandidateAttributes(candidateEntity) === 'string'">
                            <span v-if="getCandidateAttributes(candidateEntity).trim().startsWith('{') && getCandidateAttributes(candidateEntity).trim().endsWith('}')">
                              {{ JSON.stringify(JSON.parse(getCandidateAttributes(candidateEntity)), null, 2) }}
                            </span>
                            <span v-else>
                              {{ getCandidateAttributes(candidateEntity) }}
                            </span>
                          </span>
                          <span v-else>
                            {{ JSON.stringify(getCandidateAttributes(candidateEntity)) }}
                          </span>
                        </span>
                        <span v-else>-</span>
                      </div>
                    </div>
                    <!-- 显示其他重要属性 -->
                    <div v-if="candidateEntity.dynamicProperties && candidateEntity.dynamicProperties.workspace_id" class="merge-field">
                      <label>工作区ID：</label>
                      <div class="field-value">{{ candidateEntity.dynamicProperties.workspace_id }}</div>
                    </div>
                    <div v-if="candidateEntity.dynamicProperties && candidateEntity.dynamicProperties.task_id" class="merge-field">
                      <label>任务ID：</label>
                      <div class="field-value">{{ candidateEntity.dynamicProperties.task_id }}</div>
                    </div>
                    <div v-if="candidateEntity.dynamicProperties && candidateEntity.dynamicProperties.doc_id" class="merge-field">
                      <label>文档ID：</label>
                      <div class="field-value">{{ candidateEntity.dynamicProperties.doc_id }}</div>
                    </div>
                    <div v-if="candidateEntity.dynamicProperties && candidateEntity.dynamicProperties.paragraph_index" class="merge-field">
                      <label>段落索引：</label>
                      <div class="field-value">{{ candidateEntity.dynamicProperties.paragraph_index }}</div>
                    </div>
                  </div>
                </div>
              </el-col>
              
              <el-col :span="8">
                <div class="merge-section">
                  <h4>合并结果（可编辑）</h4>
                  <div class="merge-content">
                    <div class="merge-field">
                      <label>实体名称：</label>
                      <div class="field-input-container">
                        <el-input v-model="mergeForm.manualMerge.name" placeholder="编辑合并后的实体名称" />
                        <div class="form-tip">请输入合并后的实体名称</div>
                      </div>
                    </div>
                    <div class="merge-field">
                      <label>实体别名：</label>
                      <div class="field-input-container">
                        <el-input v-model="mergeForm.manualMerge.aliases" type="textarea" :rows="2" placeholder="编辑合并后的实体别名" />
                        <div class="form-tip">格式：别名1,别名2,别名3 或 JSON数组格式：["别名1","别名2"]</div>
                      </div>
                    </div>
                    <div class="merge-field">
                      <label>实体定义：</label>
                      <div class="field-input-container">
                        <el-input v-model="mergeForm.manualMerge.definition" type="textarea" :rows="3" placeholder="编辑合并后的实体定义" />
                        <div class="form-tip">请输入合并后的实体定义描述</div>
                      </div>
                    </div>
                    <div class="merge-field">
                      <label>实体属性：</label>
                      <div class="field-input-container">
                        <el-input v-model="mergeForm.manualMerge.attributes" type="textarea" :rows="4" placeholder="编辑合并后的实体属性（JSON格式）" />
                        <div class="form-tip">
                          格式：{"属性名":["值1","值2"],"属性名2":["值3"]}<br>
                          示例：{"毕业院校":["清华大学"],"工作单位":["字节跳动"],"职位":["研究员"]}
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </el-col>
            </el-row>
          </div>
        </el-card>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="compareDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleMergeEntity" :loading="mergeLoading">
            确认合并
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 候选实体详情对话框 -->
    <el-dialog title="候选实体详情" v-model="candidateDetailsVisible" width="1000px" append-to-body>
      <div v-if="candidateDetails">
        <!-- 消歧评分信息 -->
        <el-card header="消歧评分信息" style="margin-bottom: 20px;" shadow="hover">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="最终相似度">
              <el-progress
                :percentage="Math.round(candidateDetails.score?.final_score * 100)"
                :color="getScoreColor(candidateDetails.score?.final_score)"
                :stroke-width="20"
              />
              <span style="margin-left: 10px;">{{ Math.round(candidateDetails.score?.final_score * 100) }}%</span>
            </el-descriptions-item>
            <el-descriptions-item label="排名">{{ candidateDetails.rank || '-' }}</el-descriptions-item>
            <el-descriptions-item label="相似详情" :span="2">
              <div v-if="candidateDetails.similarity_details">
                <pre style="background-color: #f5f5f5; padding: 10px; border-radius: 4px; margin: 0;">{{ candidateDetails.similarity_details }}</pre>
              </div>
              <span v-else>-</span>
            </el-descriptions-item>
          </el-descriptions>
          
          <!-- 详细评分分解 -->
          <div v-if="candidateDetails.score && Object.keys(candidateDetails.score).length > 1" style="margin-top: 15px;">
            <h5>评分分解：</h5>
            <el-descriptions :column="3" border size="small">
              <el-descriptions-item 
                v-for="(value, key) in candidateDetails.score" 
                :key="key" 
                :label="key"
                v-if="key !== 'final_score'"
              >
                <span v-if="typeof value === 'number'">{{ (value * 100).toFixed(1) }}%</span>
                <span v-else>{{ value }}</span>
              </el-descriptions-item>
            </el-descriptions>
          </div>
        </el-card>

        <el-descriptions :column="2" border>
          <el-descriptions-item label="实体ID">{{ candidateDetails.id }}</el-descriptions-item>
          <el-descriptions-item label="实体名称">{{ candidateDetails.name }}</el-descriptions-item>
          <el-descriptions-item label="实体类型">{{ candidateDetails.type }}</el-descriptions-item>
          <el-descriptions-item label="实体别名">
            <div v-if="getCandidateAliases(candidateDetails)">
              <span v-if="Array.isArray(getCandidateAliases(candidateDetails))">
                {{ getCandidateAliases(candidateDetails).join(', ') }}
              </span>
              <span v-else-if="typeof getCandidateAliases(candidateDetails) === 'string'">
                <span v-if="getCandidateAliases(candidateDetails).trim().startsWith('[') && getCandidateAliases(candidateDetails).trim().endsWith(']')">
                  {{ JSON.parse(getCandidateAliases(candidateDetails)).join(', ') }}
                </span>
                <span v-else>
                  {{ getCandidateAliases(candidateDetails) }}
                </span>
              </span>
              <span v-else>
                {{ JSON.stringify(getCandidateAliases(candidateDetails)) }}
              </span>
            </div>
            <span v-else>-</span>
          </el-descriptions-item>
          <el-descriptions-item label="实体定义" :span="2">
            <div v-if="getCandidateDefinition(candidateDetails)">
              {{ getCandidateDefinition(candidateDetails) }}
            </div>
            <span v-else>-</span>
          </el-descriptions-item>
          <el-descriptions-item label="实体属性" :span="2">
            <div v-if="getCandidateAttributes(candidateDetails)">
              <pre v-if="typeof getCandidateAttributes(candidateDetails) === 'object'">{{ JSON.stringify(getCandidateAttributes(candidateDetails), null, 2) }}</pre>
              <span v-else-if="typeof getCandidateAttributes(candidateDetails) === 'string'">
                <span v-if="getCandidateAttributes(candidateDetails).trim().startsWith('{') && getCandidateAttributes(candidateDetails).trim().endsWith('}')">
                  <pre>{{ JSON.stringify(JSON.parse(getCandidateAttributes(candidateDetails)), null, 2) }}</pre>
                </span>
                <span v-else>
                  {{ getCandidateAttributes(candidateDetails) }}
                </span>
              </span>
              <span v-else>
                {{ JSON.stringify(getCandidateAttributes(candidateDetails)) }}
              </span>
            </div>
            <span v-else>-</span>
          </el-descriptions-item>
        </el-descriptions>
        
        <!-- 其他动态属性 -->
        <el-card header="其他属性" style="margin-top: 20px;" shadow="hover">
          <el-descriptions :column="3" border>
            <el-descriptions-item v-for="(value, key) in candidateDetails" :key="key" :label="key">
              <span v-if="key !== 'id' && key !== 'name' && key !== 'type' && key !== 'aliases' && key !== 'definition' && key !== 'attributes' && key !== 'relationships'">
                <span v-if="typeof value === 'object'">{{ JSON.stringify(value) }}</span>
                <span v-else>{{ value }}</span>
              </span>
            </el-descriptions-item>
          </el-descriptions>
        </el-card>
        
        <!-- 关系信息 -->
        <el-card header="关系信息" style="margin-top: 20px;" shadow="hover">
          <el-row :gutter="20">
            <el-col :span="12">
              <h5>出边关系：</h5>
              <div v-if="candidateDetails.relationships && candidateDetails.relationships.outgoing && candidateDetails.relationships.outgoing.length > 0">
                <el-table :data="candidateDetails.relationships.outgoing" size="small">
                  <el-table-column label="关系类型" prop="type" />
                  <el-table-column label="目标实体" prop="targetName" />
                </el-table>
              </div>
              <el-empty v-else description="暂无出边关系" />
            </el-col>
            <el-col :span="12">
              <h5>入边关系：</h5>
              <div v-if="candidateDetails.relationships && candidateDetails.relationships.incoming && candidateDetails.relationships.incoming.length > 0">
                <el-table :data="candidateDetails.relationships.incoming" size="small">
                  <el-table-column label="关系类型" prop="type" />
                  <el-table-column label="源实体" prop="sourceName" />
                </el-table>
              </div>
              <el-empty v-else description="暂无入边关系" />
            </el-col>
          </el-row>
        </el-card>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="candidateDetailsVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="EntityPool">
import { ref, reactive, toRefs, getCurrentInstance, watch, nextTick } from 'vue';
import {
  listEntityPool,
  getEntityPool,
  delEntityPool,
  addEntityPool,
  updateEntityPool,
  processEntity,
  disambiguateEntity,
  confirmDisambiguation,
  batchProcessEntities,
  mergeEntityInfo,
  getCandidateEntityDetails
} from "@/api/ext/extEntityPool";

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

// 实体对比和合并相关数据
const compareDialogVisible = ref(false);
const currentEntityForCompare = ref({}); // 当前选中的实体
const candidateEntity = ref({}); // 候选实体
const mergeLoading = ref(false); // 合并操作的加载状态

// 候选实体详情对话框
const candidateDetailsVisible = ref(false);
const candidateDetails = ref({});

// 合并表单
const mergeForm = ref({
  mergeStrategy: 'replace', // 默认合并策略
  fieldMerges: [], // 手动选择模式下的字段合并配置
  remark: '',
  manualMerge: { // 手动合并模式下的字段值
    name: '',
    aliases: '',
    definition: '',
    attributes: ''
  }
});

// 表单参数
const form = ref({
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
  processTime: null,
  processorId: null,
  processBy: null,
  processRemark: null,
  validFlag: 1,
  delFlag: 0,
  createBy: null,
  creatorId: null,
  createTime: null,
  updateBy: null,
  updaterId: null,
  updateTime: null,
  remark: null
});

// 查询参数
const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  workspaceId: null,
  taskId: null,
  entityName: null,
  entityType: null,
  status: null,
  createTime: null,
});

// 处理表单
const processForm = ref({
  id: null,
  status: null,
  remark: null,
});

// 确认表单
const confirmForm = ref({
  entityPoolId: null,
  candidateId: null,
  remark: null,
});

// 处理表单校验规则
const processRules = ref({
  status: [{ required: true, message: "处理状态不能为空", trigger: "change" }],
});

// 确认表单校验规则
const confirmRules = ref({
  candidateId: [{ required: true, message: "候选实体不能为空", trigger: "change" }],
});

// 表单校验规则
const rules = ref({
  workspaceId: [
    { required: true, message: "工作区ID不能为空", trigger: "blur" },
    { type: "number", message: "工作区ID必须为数字", trigger: "blur" }
  ],
  taskId: [
    { required: true, message: "任务ID不能为空", trigger: "blur" },
    { type: "number", message: "任务ID必须为数字", trigger: "blur" }
  ],
  docId: [
    { required: true, message: "文档ID不能为空", trigger: "blur" },
    { type: "number", message: "文档ID必须为数字", trigger: "blur" }
  ],
  paragraphIndex: [
    { required: true, message: "段落索引不能为空", trigger: "blur" },
    { type: "number", message: "段落索引必须为数字", trigger: "blur" }
  ],
  entityId: [
    { required: true, message: "实体ID不能为空", trigger: "blur" },
    { pattern: /^[a-zA-Z_][a-zA-Z0-9_]*$/, message: "实体ID格式不正确，只能包含字母、数字和下划线，且不能以数字开头", trigger: "blur" }
  ],
  entityName: [
    { required: true, message: "实体名称不能为空", trigger: "blur" },
    { min: 1, max: 255, message: "实体名称长度在 1 到 255 个字符", trigger: "blur" }
  ],
  entityType: [
    { required: true, message: "实体类型不能为空", trigger: "change" }
  ],
  aliases: [
    { 
      validator: (rule, value, callback) => {
        if (value && value.trim() !== '') {
          // 检查是否为JSON数组格式
          if (value.trim().startsWith('[') && value.trim().endsWith(']')) {
            try {
              JSON.parse(value);
            } catch (e) {
              callback(new Error('别名JSON格式不正确'));
              return;
            }
          }
          // 检查是否为逗号分隔格式
          else if (value.includes(',')) {
            const aliases = value.split(',').map(a => a.trim()).filter(a => a !== '');
            if (aliases.length === 0) {
              callback(new Error('别名格式不正确'));
              return;
            }
          }
        }
        callback();
      },
      trigger: "blur"
    }
  ],
  attributes: [
    { 
      validator: (rule, value, callback) => {
        if (value && value.trim() !== '') {
          try {
            const parsed = JSON.parse(value);
            if (typeof parsed !== 'object' || parsed === null) {
              callback(new Error('属性必须是JSON对象格式'));
              return;
            }
            // 检查所有值是否为数组
            for (const key in parsed) {
              if (!Array.isArray(parsed[key])) {
                callback(new Error(`属性"${key}"的值必须是数组格式`));
                return;
              }
            }
          } catch (e) {
            callback(new Error('属性JSON格式不正确'));
            return;
          }
        }
        callback();
      },
      trigger: "blur"
    }
  ]
});

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
      const formData = processFormData();
      
      if (formData.id != null) {
        updateEntityPool(formData).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addEntityPool(formData).then(response => {
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

/** 批量确认 */
function handleBatchConfirm() {
  if (ids.value.length === 0) {
    proxy.$modal.msgError("请选择要批量确认的实体池");
    return;
  }
  proxy.$modal.confirm('是否确认批量确认选中的实体池？').then(function() {
    return batchProcessEntities(ids.value, 1, ''); // 批量确认，状态为1
  }).then(response => {
    console.log('批量确认响应:', response);
    if (response.code === 200) {
      const data = response.data;
      if (data.failCount === 0) {
        proxy.$modal.msgSuccess(data.message || "批量确认成功");
      } else if (data.successCount === 0) {
        proxy.$modal.msgError(data.message || "批量确认失败");
      } else {
        proxy.$modal.msgWarning(data.message || "批量确认部分成功");
      }
      // 显示详细结果
      if (data.errorMessages && data.errorMessages.length > 0) {
        console.log('失败详情:', data.errorMessages);
        // 可以选择弹窗显示详细错误信息
        proxy.$modal.msgWarning("部分处理失败，请查看控制台了解详情");
      }
    } else {
      proxy.$modal.msgError(response.msg || "批量确认失败");
    }
    getList();
  }).catch(error => {
    console.error('批量确认失败:', error);
    proxy.$modal.msgError("批量确认失败: " + (error.message || '未知错误'));
  });
}

/** 批量拒绝 */
function handleBatchReject() {
  if (ids.value.length === 0) {
    proxy.$modal.msgError("请选择要批量拒绝的实体池");
    return;
  }
  proxy.$modal.confirm('是否确认批量拒绝选中的实体池？').then(function() {
    return batchProcessEntities(ids.value, 2, ''); // 批量拒绝，状态为2
  }).then(response => {
    console.log('批量拒绝响应:', response);
    if (response.code === 200) {
      const data = response.data;
      if (data.failCount === 0) {
        proxy.$modal.msgSuccess(data.message || "批量拒绝成功");
      } else if (data.successCount === 0) {
        proxy.$modal.msgError(data.message || "批量拒绝失败");
      } else {
        proxy.$modal.msgWarning(data.message || "批量拒绝部分成功");
      }
      // 显示详细结果
      if (data.errorMessages && data.errorMessages.length > 0) {
        console.log('失败详情:', data.errorMessages);
        // 可以选择弹窗显示详细错误信息
        proxy.$modal.msgWarning("部分处理失败，请查看控制台了解详情");
      }
    } else {
      proxy.$modal.msgError(response.msg || "批量拒绝失败");
    }
    getList();
  }).catch(error => {
    console.error('批量拒绝失败:', error);
    proxy.$modal.msgError("批量拒绝失败: " + (error.message || '未知错误'));
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

/** 确认候选实体 */
function handleConfirmCandidate(candidate) {
  console.log('handleConfirmCandidate: 开始处理候选实体', candidate);
  
  selectedCandidate.value = candidate;
  currentEntityForCompare.value = currentEntity.value; // 设置当前实体
  candidateEntity.value = candidate.entity; // 设置候选实体
  
  // 调试信息：输出候选实体的完整数据结构
  console.log('候选实体完整数据:', candidate);
  console.log('候选实体基本信息:', candidate.entity);
  console.log('候选实体动态属性:', candidate.entity?.dynamicProperties);
  console.log('当前实体信息:', currentEntity.value);
  console.log('设置后的candidateEntity:', candidateEntity.value);
  
  // 测试辅助函数
  console.log('=== 辅助函数测试 ===');
  console.log('getCandidateAliases结果:', getCandidateAliases(candidate.entity));
  console.log('getCandidateDefinition结果:', getCandidateDefinition(candidate.entity));
  console.log('getCandidateAttributes结果:', getCandidateAttributes(candidate.entity));
  console.log('=== 辅助函数测试结束 ===');
  
  // 初始化合并表单
  mergeForm.value.mergeStrategy = 'replace';
  mergeForm.value.remark = '';
  mergeForm.value.manualMerge = {
    name: '',
    aliases: '',
    definition: '',
    attributes: ''
  };
  
  // 初始化手动合并字段值 - 使用智能合并算法
  if (candidate.entity) {
    // 确保变量已经设置完成后再执行智能合并
    nextTick(() => {
      console.log('nextTick: 准备执行智能合并');
      // 执行智能合并并填充默认值，传递具体的实体数据
      performSmartMerge(currentEntity.value, candidate.entity);
      
      // 输出智能合并的调试信息
      console.log('=== 智能合并结果 ===');
      console.log('实体名称合并结果:', mergeForm.value.manualMerge.name);
      console.log('实体别名合并结果:', mergeForm.value.manualMerge.aliases);
      console.log('实体定义合并结果:', mergeForm.value.manualMerge.definition);
      console.log('实体属性合并结果:', mergeForm.value.manualMerge.attributes);
      console.log('=== 智能合并结果结束 ===');
    });
  }
  
  // 调试信息：输出初始化后的合并表单
  console.log('初始化后的合并表单:', mergeForm.value.manualMerge);
  
  compareDialogVisible.value = true; // 打开对比对话框
}

/** 初始化字段合并配置 */
function initializeFieldMerges() {
  const currentEntity = currentEntityForCompare.value;
  const candidateEntity = candidateEntity.value;
  
  const fieldMerges = [];
  
  // 添加基本字段
  const fields = [
    { name: 'name', label: '实体名称' },
    { name: 'aliases', label: '实体别名' },
    { name: 'definition', label: '实体定义' },
    { name: 'attributes', label: '实体属性' }
  ];
  
  fields.forEach(field => {
    const currentValue = currentEntity[field.name];
    const candidateValue = candidateEntity.dynamicProperties ? candidateEntity.dynamicProperties[field.name] : null;
    
    fieldMerges.push({
      fieldName: field.name,
      fieldLabel: field.label,
      currentValue: formatFieldValue(currentValue),
      candidateValue: formatFieldValue(candidateValue),
      mergeType: 'merge' // 默认合并
    });
  });
  
  mergeForm.value.fieldMerges = fieldMerges;
}

/** 格式化字段值用于显示 */
function formatFieldValue(value) {
  if (value === null || value === undefined) {
    return '-';
  }
  
  if (typeof value === 'object') {
    return JSON.stringify(value, null, 2);
  }
  
  if (Array.isArray(value)) {
    return value.join(', ');
  }
  
  return String(value);
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

/** 实体合并 */
function handleMergeEntity() {
  if (!currentEntityForCompare.value || !candidateEntity.value) {
    proxy.$modal.msgError("请先选择实体进行对比。");
    return;
  }

  const mergeStrategy = mergeForm.value.mergeStrategy;
  let mergeFields = [];

  if (mergeStrategy === 'manual') {
    // 手动合并模式：使用用户编辑的合并结果
    const manualMerge = mergeForm.value.manualMerge;
    
    // 验证手动合并结果
    if (!manualMerge.name || manualMerge.name.trim() === '') {
      proxy.$modal.msgError("请填写合并后的实体名称。");
      return;
    }
    
    // 构建手动合并的字段配置
    mergeFields = [
      { fieldName: 'name', mergeType: 'manual', value: manualMerge.name },
      { fieldName: 'aliases', mergeType: 'manual', value: manualMerge.aliases },
      { fieldName: 'definition', mergeType: 'manual', value: manualMerge.definition },
      { fieldName: 'attributes', mergeType: 'manual', value: manualMerge.attributes }
    ];
  } else {
    // 自动合并策略，尝试合并所有字段
    const fields = ['name', 'aliases', 'definition', 'attributes'];
    mergeFields = fields.map(fieldName => ({
      fieldName: fieldName,
      mergeType: mergeStrategy
    }));
  }

  if (mergeFields.length === 0) {
    proxy.$modal.msgError("没有可合并的字段。");
    return;
  }

  // 添加调试日志
  console.log('=== 实体合并调试信息 ===');
  console.log('当前实体ID:', currentEntityForCompare.value.id);
  console.log('候选实体ID:', candidateEntity.value.id);
  console.log('候选实体完整数据:', candidateEntity.value);
  console.log('合并字段:', mergeFields);
  console.log('合并备注:', mergeForm.value.remark);
  console.log('=== 实体合并调试信息结束 ===');

  mergeLoading.value = true;
  
  // 调用合并API
  mergeEntityInfo(
    currentEntityForCompare.value.id,
    candidateEntity.value.id,
    mergeFields,
    mergeForm.value.remark
  ).then(response => {
    if (response.code === 200) {
      proxy.$modal.msgSuccess("实体合并成功");
      compareDialogVisible.value = false;
      getList();
      // 重置表单
      mergeForm.value.mergeStrategy = 'replace';
      mergeForm.value.fieldMerges = [];
      mergeForm.value.remark = '';
      mergeForm.value.manualMerge = {
        name: '',
        aliases: '',
        definition: '',
        attributes: ''
      };
    } else {
      proxy.$modal.msgError(response.msg || "实体合并失败");
    }
  }).catch(error => {
    console.error('实体合并失败:', error);
    proxy.$modal.msgError("实体合并失败: " + (error.message || '未知错误'));
  }).finally(() => {
    mergeLoading.value = false;
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

/** 查看候选实体详情 */
function handleViewCandidateDetails(candidate) {
  // 如果传入的是候选实体对象，直接使用
  if (candidate && candidate.entity) {
    candidateDetails.value = {
      ...candidate.entity,
      score: candidate.score,
      rank: candidate.rank,
      similarity_details: candidate.similarity_details
    };
    candidateDetailsVisible.value = true;
  } else {
    // 兼容旧的方式，通过ID获取详情
    getCandidateEntityDetails(candidate).then(response => {
      if (response.code === 200) {
        candidateDetails.value = response.data;
        candidateDetailsVisible.value = true;
      } else {
        proxy.$modal.msgError(response.msg || "获取候选实体详情失败");
      }
    }).catch(error => {
      console.error('获取候选实体详情失败:', error);
      proxy.$modal.msgError("获取候选实体详情失败: " + (error.message || '未知错误'));
    });
  }
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
    processTime: null,
    processorId: null,
    processBy: null,
    processRemark: null,
    validFlag: 1,
    delFlag: 0,
    createBy: null,
    creatorId: null,
    createTime: null,
    updateBy: null,
    updaterId: null,
    updateTime: null,
    remark: null
  };
  proxy.resetForm("entityPoolRef");
}

// 取消按钮
function cancel() {
  open.value = false;
  reset();
}

// 格式化别名输入
function formatAliases(value) {
  if (!value) return '';
  // 如果是JSON数组格式，转换为逗号分隔
  if (value.trim().startsWith('[') && value.trim().endsWith(']')) {
    try {
      const parsed = JSON.parse(value);
      return parsed.join(',');
    } catch (e) {
      return value;
    }
  }
  return value;
}

// 格式化属性输入
function formatAttributes(value) {
  if (!value) return '';
  // 如果输入的是简单格式，转换为JSON格式
  if (!value.trim().startsWith('{')) {
    try {
      // 尝试解析为JSON，如果失败则保持原样
      JSON.parse(value);
      return value;
    } catch (e) {
      // 不是JSON格式，保持原样
      return value;
    }
  }
  return value;
}

// 表单提交前的数据处理
function processFormData() {
  const formData = { ...form.value };
  
  // 处理别名格式
  if (formData.aliases) {
    formData.aliases = formatAliases(formData.aliases);
  }
  
  // 处理属性格式
  if (formData.attributes) {
    formData.attributes = formatAttributes(formData.attributes);
  }
  
  return formData;
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

// 合并策略变化时重置手动合并字段
function handleMergeStrategyChange() {
  console.log('handleMergeStrategyChange: 合并策略变化', mergeForm.value.mergeStrategy);
  
  if (mergeForm.value.mergeStrategy === 'manual') {
    // 检查必要的变量是否已设置
    if (currentEntityForCompare.value && candidateEntity.value) {
      console.log('handleMergeStrategyChange: 执行智能合并');
      // 手动模式下，执行智能合并并填充默认值
      performSmartMerge(currentEntityForCompare.value, candidateEntity.value);
    } else {
      console.log('handleMergeStrategyChange: 变量未设置，跳过智能合并', {
        currentEntityForCompare: currentEntityForCompare.value,
        candidateEntity: candidateEntity.value
      });
    }
  }
}

// 智能合并算法
function performSmartMerge(currentEntityData = null, candidateEntityData = null) {
  // 如果没有传递参数，则使用全局变量
  const currentEntity = currentEntityData || currentEntityForCompare.value;
  const candidateEntity = candidateEntityData || candidateEntity.value;
  
  if (!currentEntity || !candidateEntity) {
    console.log('performSmartMerge: 实体数据不完整', { currentEntity, candidateEntity });
    return;
  }
  
  console.log('performSmartMerge: 开始智能合并', { currentEntity, candidateEntity });
  
  // 智能合并实体名称
  const mergedName = smartMergeString(
    currentEntity.entityName,
    candidateEntity.name
  );
  mergeForm.value.manualMerge.name = mergedName;
  console.log('智能合并实体名称:', { current: currentEntity.entityName, candidate: candidateEntity.name, merged: mergedName });
  
  // 智能合并实体别名
  const currentAliases = getCandidateAliases(currentEntity);
  const candidateAliases = getCandidateAliases(candidateEntity);
  const mergedAliases = smartMergeArray(
    currentAliases,
    candidateAliases
  );
  mergeForm.value.manualMerge.aliases = mergedAliases;
  console.log('智能合并实体别名:', { current: currentAliases, candidate: candidateAliases, merged: mergedAliases });
  
  // 智能合并实体定义
  const currentDefinition = currentEntity.definition;
  const candidateDefinition = getCandidateDefinition(candidateEntity);
  const mergedDefinition = smartMergeDefinition(
    currentDefinition,
    candidateDefinition
  );
  mergeForm.value.manualMerge.definition = mergedDefinition;
  console.log('智能合并实体定义:', { current: currentDefinition, candidate: candidateDefinition, merged: mergedDefinition });
  
  // 智能合并实体属性
  const currentAttributes = getCandidateAttributes(currentEntity);
  const candidateAttributes = getCandidateAttributes(candidateEntity);
  const mergedAttributes = smartMergeObject(
    currentAttributes,
    candidateAttributes
  );
  mergeForm.value.manualMerge.attributes = mergedAttributes;
  console.log('智能合并实体属性:', { current: currentAttributes, candidate: candidateAttributes, merged: mergedAttributes });
  
  console.log('performSmartMerge: 智能合并完成', mergeForm.value.manualMerge);
}

// 智能合并实体定义
function smartMergeDefinition(def1, def2) {
  if (!def1 && !def2) return '';
  if (!def1) return def2;
  if (!def2) return def1;
  
  // 去除首尾空格
  const d1 = def1.toString().trim();
  const d2 = def2.toString().trim();
  
  // 如果两个定义相同，直接返回
  if (d1 === d2) return d1;
  
  // 如果其中一个包含另一个，返回较长的（通常包含更多信息）
  if (d1.includes(d2)) return d1;
  if (d2.includes(d1)) return d2;
  
  // 如果两个定义都很短，用分隔符连接
  if (d1.length < 50 && d2.length < 50) {
    return `${d1}；${d2}`;
  }
  
  // 如果其中一个明显更长，优先使用较长的（通常包含更多信息）
  if (d1.length > d2.length * 1.5) return d1;
  if (d2.length > d1.length * 1.5) return d2;
  
  // 否则用分隔符连接，但使用更自然的分隔符
  return `${d1}。${d2}`;
}

// 智能合并字符串
function smartMergeString(str1, str2) {
  if (!str1 && !str2) return '';
  if (!str1) return str2;
  if (!str2) return str1;
  
  // 去除首尾空格
  const s1 = str1.toString().trim();
  const s2 = str2.toString().trim();
  
  // 如果两个字符串相同，直接返回
  if (s1 === s2) return s1;
  
  // 如果其中一个包含另一个，返回较长的
  if (s1.includes(s2)) return s1;
  if (s2.includes(s1)) return s2;
  
  // 对于实体名称，优先使用候选实体的名称（通常更准确）
  if (s1 && s2) {
    return s2; // 优先使用候选实体的名称
  }
  
  // 否则用分隔符连接
  return `${s1} | ${s2}`;
}

// 智能合并数组
function smartMergeArray(arr1, arr2) {
  if (!arr1 && !arr2) return '';
  if (!arr1) return Array.isArray(arr2) ? arr2.join(', ') : arr2;
  if (!arr2) return Array.isArray(arr1) ? arr1.join(', ') : arr1;
  
  // 确保都是数组
  const array1 = Array.isArray(arr1) ? arr1 : [arr1];
  const array2 = Array.isArray(arr2) ? arr2 : [arr2];
  
  // 合并数组并去重
  const merged = [...new Set([...array1, ...array2])];
  
  // 过滤空值
  const filtered = merged.filter(item => item && item.toString().trim() !== '');
  
  return filtered.join(', ');
}

// 智能合并对象
function smartMergeObject(obj1, obj2) {
  if (!obj1 && !obj2) return '';
  if (!obj1) return typeof obj2 === 'object' ? JSON.stringify(obj2, null, 2) : obj2;
  if (!obj2) return typeof obj1 === 'object' ? JSON.stringify(obj1, null, 2) : obj1;
  
  // 确保都是对象
  const object1 = typeof obj1 === 'object' ? obj1 : {};
  const object2 = typeof obj2 === 'object' ? obj2 : {};
  
  // 合并对象
  const merged = { ...object1 };
  
  for (const key in object2) {
    if (object2.hasOwnProperty(key)) {
      if (merged[key]) {
        // 如果键已存在，合并值
        if (Array.isArray(merged[key]) && Array.isArray(object2[key])) {
          // 两个都是数组，合并并去重
          merged[key] = [...new Set([...merged[key], ...object2[key]])];
        } else if (Array.isArray(merged[key])) {
          // 当前是数组，新值是单个值
          if (!merged[key].includes(object2[key])) {
            merged[key].push(object2[key]);
          }
        } else if (Array.isArray(object2[key])) {
          // 新值是数组，当前是单个值
          if (!object2[key].includes(merged[key])) {
            merged[key] = [merged[key], ...object2[key]];
          } else {
            merged[key] = object2[key];
          }
        } else {
          // 两个都是单个值，用分隔符连接
          if (merged[key] !== object2[key]) {
            merged[key] = `${merged[key]} | ${object2[key]}`;
          }
        }
      } else {
        // 键不存在，直接添加
        merged[key] = object2[key];
      }
    }
  }
  
  return JSON.stringify(merged, null, 2);
}

// 辅助函数：获取候选实体的别名
function getCandidateAliases(entity) {
  if (entity) {
    // 首先检查是否有dynamicProperties结构
    if (entity.dynamicProperties && entity.dynamicProperties.aliases) {
      if (Array.isArray(entity.dynamicProperties.aliases)) {
        return entity.dynamicProperties.aliases;
      } else if (typeof entity.dynamicProperties.aliases === 'string') {
        if (entity.dynamicProperties.aliases.trim().startsWith('[') && entity.dynamicProperties.aliases.trim().endsWith(']')) {
          try {
            return JSON.parse(entity.dynamicProperties.aliases);
          } catch (e) {
            return entity.dynamicProperties.aliases;
          }
        } else {
          return entity.dynamicProperties.aliases;
        }
      }
    }
    // 检查扁平结构
    else if (entity.aliases) {
      if (Array.isArray(entity.aliases)) {
        return entity.aliases;
      } else if (typeof entity.aliases === 'string') {
        if (entity.aliases.trim().startsWith('[') && entity.aliases.trim().endsWith(']')) {
          try {
            return JSON.parse(entity.aliases);
          } catch (e) {
            return entity.aliases;
          }
        } else {
          return entity.aliases;
        }
      }
    }
  }
  return null;
}

// 辅助函数：获取候选实体的定义
function getCandidateDefinition(entity) {
  if (entity) {
    // 首先检查是否有dynamicProperties结构
    if (entity.dynamicProperties && entity.dynamicProperties.definition) {
      return entity.dynamicProperties.definition;
    }
    // 检查扁平结构
    else if (entity.definition) {
      return entity.definition;
    }
  }
  return null;
}

// 辅助函数：获取候选实体的属性
function getCandidateAttributes(entity) {
  if (entity) {
    // 首先检查是否有dynamicProperties结构
    if (entity.dynamicProperties && entity.dynamicProperties.attributes) {
      if (typeof entity.dynamicProperties.attributes === 'object') {
        return entity.dynamicProperties.attributes;
      } else if (typeof entity.dynamicProperties.attributes === 'string') {
        if (entity.dynamicProperties.attributes.trim().startsWith('{') && entity.dynamicProperties.attributes.trim().endsWith('}')) {
          try {
            return JSON.parse(entity.dynamicProperties.attributes);
          } catch (e) {
            return entity.dynamicProperties.attributes;
          }
        } else {
          return entity.dynamicProperties.attributes;
        }
      }
    }
    // 检查扁平结构
    else if (entity.attributes) {
      if (typeof entity.attributes === 'object') {
        return entity.attributes;
      } else if (typeof entity.attributes === 'string') {
        if (entity.attributes.trim().startsWith('{') && entity.attributes.trim().endsWith('}')) {
          try {
            return JSON.parse(entity.attributes);
          } catch (e) {
            return entity.attributes;
          }
        } else {
          return entity.attributes;
        }
      }
    }
  }
  return null;
}

// 显示候选实体的调试信息
function showCandidateDebugInfo() {
  const debugInfo = {
    candidateEntity: candidateEntity.value,
    dynamicProperties: candidateEntity.value?.dynamicProperties,
    aliases: getCandidateAliases(candidateEntity.value),
    definition: getCandidateDefinition(candidateEntity.value),
    attributes: getCandidateAttributes(candidateEntity.value),
    rawAliases: candidateEntity.value?.dynamicProperties?.aliases || candidateEntity.value?.aliases,
    rawDefinition: candidateEntity.value?.dynamicProperties?.definition || candidateEntity.value?.definition,
    rawAttributes: candidateEntity.value?.dynamicProperties?.attributes || candidateEntity.value?.attributes
  };
  
  // 使用console.log输出调试信息
  console.log('候选实体调试信息:', debugInfo);
  
  // 使用alert显示调试信息（简化版本）
  const debugText = JSON.stringify(debugInfo, null, 2);
  alert('候选实体调试信息已输出到控制台，请按F12查看');
  
  // 尝试复制到剪贴板
  try {
    navigator.clipboard.writeText(debugText).then(() => {
      proxy.$modal.msgSuccess('调试信息已复制到剪贴板');
    }).catch(() => {
      proxy.$modal.msgError('复制失败');
    });
  } catch (error) {
    proxy.$modal.msgError('复制功能不可用');
  }
}

// 重置为智能合并的默认值
function resetToSmartMerge() {
  console.log('resetToSmartMerge: 重置为智能合并');
  
  // 检查必要的变量是否已设置
  if (currentEntityForCompare.value && candidateEntity.value) {
    performSmartMerge(currentEntityForCompare.value, candidateEntity.value);
    proxy.$modal.msgSuccess('已重置为智能合并的默认值');
  } else {
    console.log('resetToSmartMerge: 变量未设置，无法执行智能合并', {
      currentEntityForCompare: currentEntityForCompare.value,
      candidateEntity: candidateEntity.value
    });
    proxy.$modal.msgError('无法执行智能合并，请重新选择实体');
  }
}

getList();
</script>

<style scoped>
.form-tip {
  font-size: 12px;
  color: #909399;
  line-height: 1.4;
  margin-top: 4px;
  padding: 4px 8px;
  background-color: #f5f7fa;
  border-radius: 4px;
  border-left: 3px solid #409eff;
}

.el-form-item {
  margin-bottom: 20px;
}

.el-dialog {
  border-radius: 8px;
}

.el-dialog__header {
  background-color: #f5f7fa;
  border-bottom: 1px solid #e4e7ed;
}

.el-dialog__title {
  font-weight: 600;
  color: #303133;
}

.dialog-footer {
  text-align: right;
  padding-top: 20px;
  border-top: 1px solid #e4e7ed;
}

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

.merge-editor {
  display: flex;
  gap: 20px;
  padding: 10px;
}

.merge-section {
  flex: 1;
  background-color: #f5f7fa;
  border-radius: 4px;
  padding: 15px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.merge-section h4 {
  margin-top: 0;
  margin-bottom: 15px;
  color: #303133;
  font-size: 16px;
  font-weight: 600;
}

.merge-content {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.merge-field {
  display: flex;
  flex-direction: column;
  padding: 8px 0;
  border-bottom: 1px dashed #e4e7ed;
}

.merge-field:last-child {
  border-bottom: none;
}

.merge-field label {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
  margin-bottom: 8px;
}

.field-input-container {
  display: flex;
  flex-direction: column;
  width: 100%;
}

.field-input-container .el-input,
.field-input-container .el-textarea {
  margin-bottom: 4px;
}

.field-value {
  flex: 1;
  font-size: 14px;
  color: #303133;
  word-break: break-all;
  white-space: pre-wrap;
  line-height: 1.6;
}

.el-input.field-value {
  width: 300px; /* Adjust as needed */
}

.el-textarea.field-value {
  width: 300px; /* Adjust as needed */
  min-height: 80px; /* Adjust as needed */
}

.merge-tip {
  font-size: 12px;
  color: #909399;
  line-height: 1.4;
  margin-top: 4px;
  padding: 4px 8px;
  background-color: #f0f9ff;
  border: 1px solid #b3d8ff;
  border-radius: 4px;
}
</style>

 