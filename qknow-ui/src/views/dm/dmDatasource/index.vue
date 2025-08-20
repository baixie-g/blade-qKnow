<template>
    <div class="app-container" ref="app-container">
        <div class="pagecont-top" v-show="showSearch">
            <el-form
                class="btn-style"
                :model="queryParams"
                ref="queryRef"
                :inline="true"
                label-width="100px"
                v-show="showSearch"
                @submit.prevent
            >
                <el-form-item label="数据连接名称" prop="datasourceName">
                    <el-input
                        class="el-form-input-width"
                        v-model="queryParams.datasourceName"
                        placeholder="请输入数据连接名称"
                        clearable
                        @keyup.enter="handleQuery"
                    />
                </el-form-item>
                <el-form-item label="数据源类型" prop="datasourceType">
                    <el-select
                        class="el-form-input-width"
                        v-model="queryParams.datasourceType"
                        placeholder="请选择数据源类型"
                        clearable
                    >
                        <el-option
                            v-for="dict in datasourceTypeOptions"
                            :key="dict.value"
                            :label="dict.label"
                            :value="dict.value"
                        />
                    </el-select>
                </el-form-item>
                <el-form-item>
                    <el-button
                        plain
                        type="primary"
                        @click="handleQuery"
                        @mousedown="(e) => e.preventDefault()"
                    >
                        <i class="iconfont-mini icon-a-zu22377 mr5"></i>查询
                    </el-button>
                    <el-button @click="resetQuery" @mousedown="(e) => e.preventDefault()">
                        <i class="iconfont-mini icon-a-zu22378 mr5"></i>重置
                    </el-button>
                </el-form-item>
            </el-form>
        </div>

        <div class="pagecont-bottom">
            <div class="justify-between mb15">
                <el-row :gutter="15" class="btn-style">
                    <el-col :span="1.5">
                        <el-button
                            type="primary"
                            plain
                            @click="handleAdd"
                            v-hasPermi="['dm:datasource:datasource:add']"
                            @mousedown="(e) => e.preventDefault()"
                        >
                            <i class="iconfont-mini icon-xinzeng mr5"></i>新增
                        </el-button>
                    </el-col>
                    <el-col :span="1.5">
                        <el-button type="info" plain @click="handleUploadToNacos" @mousedown="(e) => e.preventDefault()">
                            <i class="iconfont-mini icon-upload-cloud-line mr5"></i>上传到Nacos
                        </el-button>
                    </el-col>
                    <!--         <el-col :span="1.5">-->
                    <!--           <el-button type="primary" plain :disabled="single" @click="handleUpdate" v-hasPermi="['dm:datasource:datasource:edit']"-->
                    <!--                      @mousedown="(e) => e.preventDefault()">-->
                    <!--             <i class="iconfont-mini icon-xiugai&#45;&#45;copy mr5"></i>修改-->
                    <!--           </el-button>-->
                    <!--         </el-col>-->
                    <!--         <el-col :span="1.5">-->
                    <!--           <el-button type="danger" plain :disabled="multiple" @click="handleDelete" v-hasPermi="['dm:datasource:datasource:remove']"-->
                    <!--                      @mousedown="(e) => e.preventDefault()">-->
                    <!--             <i class="iconfont-mini icon-shanchu-huise mr5"></i>删除-->
                    <!--           </el-button>-->
                    <!--         </el-col>-->
                </el-row>
                <div class="justify-end top-right-btn">
                    <right-toolbar
                        v-model:showSearch="showSearch"
                        @queryTable="getList"
                        :columns="columns"
                    ></right-toolbar>
                </div>
            </div>
            <el-table
                stripe
                height="590px"
                v-loading="loading"
                :data="daDatasourceList"
                @selection-change="handleSelectionChange"
                :default-sort="defaultSort"
                @sort-change="handleSortChange"
            >
<!--                <el-table-column-->
<!--                    v-if="getColumnVisibility(1)"-->
<!--                    label="编号"-->
<!--                    align="center"-->
<!--                    prop="id"-->
<!--                    show-overflow-tooltip-->
<!--                >-->
<!--                    <template #default="scope">-->
<!--                        {{ scope.row.id || '-' }}-->
<!--                    </template>-->
<!--                </el-table-column>-->
                <!--       <el-table-column type="selection" width="55" align="center" />-->
                <el-table-column label="序号" align="center" width="80">
                  <template #default="{ $index }">
                    {{ $index + 1 }}
                  </template>
                </el-table-column>
                <el-table-column
                    v-if="getColumnVisibility(1)"
                    label="数据连接名称"
                    align="center"
                    prop="datasourceName"
                    show-overflow-tooltip
                >
                    <template #default="scope">
                        {{ scope.row.datasourceName || '-' }}
                    </template>
                </el-table-column>
                <el-table-column
                    v-if="getColumnVisibility(9)"
                    label="数据连接描述"
                    align="center"
                    prop="description"
                    show-overflow-tooltip
                >
                    <template #default="scope">
                        {{ scope.row.description || '-' }}
                    </template>
                </el-table-column>
                <el-table-column
                    v-if="getColumnVisibility(4)"
                    label="数据库IP"
                    align="center"
                    prop="ip"
                    show-overflow-tooltip
                >
                    <template #default="scope">
                        {{ scope.row.ip || '-' }}
                    </template>
                </el-table-column>
                <el-table-column
                    v-if="getColumnVisibility(2)"
                    label="数据源类型"
                    align="center"
                    prop="datasourceType"
                >
                    <template #default="scope">
                        <span v-if="scope.row.datasourceType === 1">MySQL</span>
                        <span v-else-if="scope.row.datasourceType === 2">Neo4j</span>
                        <span v-else-if="scope.row.datasourceType === 3">Oracle</span>
                        <span v-else-if="scope.row.datasourceType === 4">PostgreSQL</span>
                        <span v-else>{{ scope.row.datasourceType }}</span>
                    </template>
                </el-table-column>



                <el-table-column
                    v-if="getColumnVisibility(13)"
                    label="创建时间"
                    align="center"
                    prop="createTime"
                    width="180"
                    sortable="custom"
                    :sort-orders="['descending', 'ascending']"
                >
                    <template #default="scope">
                        <span>{{
                            parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}:{s}')
                        }}</span>
                    </template>
                </el-table-column>
                <el-table-column
                    v-if="getColumnVisibility(14)"
                    label="更新时间"
                    align="center"
                    prop="updateTime"
                    width="180"
                >
                    <template #default="scope">
                        <span>{{
                            parseTime(scope.row.updateTime, '{y}-{m}-{d} {h}:{i}:{s}')
                        }}</span>
                    </template>
                </el-table-column>

                <el-table-column
                    label="操作"
                    align="center"
                    class-name="small-padding fixed-width"
                    fixed="right"
                    width="350"
                >
                    <template #default="scope">
                        <el-button
                            link
                            type="primary"
                            icon="view"
                            @click="handleTestConnection(scope.row)"
                            v-hasPermi="['dm:datasource:datasource:edit']"
                            >测试连接</el-button
                        >
                        <el-button
                            link
                            type="primary"
                            icon="Edit"
                            @click="handleUpdate(scope.row)"
                            v-hasPermi="['dm:datasource:datasource:edit']"
                            >修改</el-button
                        >

                        <el-button
                            link
                            type="danger"
                            icon="Delete"
                            @click="handleDelete(scope.row)"
                            v-hasPermi="['dm:datasource:datasource:remove']"
                            >删除</el-button
                        >
                        <el-button
                            link
                            type="primary"
                            icon="view"
                            @click="handleDetail(scope.row)"
                            v-hasPermi="['dm:datasource:datasource:edit']"
                            >详情</el-button
                        >
                        <!--           <el-button link type="primary" icon="view" @click="routeTo('/da/datasource/daDatasourceDetail',scope.row)"-->
                        <!--                      v-hasPermi="['dm:datasource:datasource:edit']">复杂详情</el-button>-->
                    </template>
                </el-table-column>

                <template #empty>
                    <div class="emptyBg">
                        <img src="@/assets/system/images/no_data/noData.png" alt="" />
                        <p>暂无记录</p>
                    </div>
                </template>
            </el-table>

            <pagination
                v-show="total > 0"
                :total="total"
                v-model:page="queryParams.pageNum"
                v-model:limit="queryParams.pageSize"
                @pagination="getList"
            />
        </div>

        <!-- 添加或修改数据源对话框 -->
        <el-dialog
            :title="title"
            v-model="open"
            width="800px"
            :append-to="$refs['app-container']"
            draggable
        >
            <template #header="{ close, titleId, titleClass }">
                <span role="heading" aria-level="2" class="el-dialog__title">
                    {{ title }}
                </span>
            </template>
            <el-form
                ref="daDatasourceRef"
                :model="form"
                :rules="rules"
                label-width="110px"
                @submit.prevent
            >
                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="数据连接名称" prop="datasourceName">
                            <el-input
                                v-model="form.datasourceName"
                                placeholder="请输入数据连接名称"
                            />
                        </el-form-item>
                    </el-col>

                    <el-col :span="12">
                        <el-form-item label="数据源类型" prop="datasourceType">
                            <el-select v-model="form.datasourceType" placeholder="请选择数据源类型">
                                <el-option
                                    v-for="dict in datasourceTypeOptions"
                                    :key="dict.value"
                                    :label="dict.label"
                                    :value="dict.value"
                                ></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="账号" prop="username">
                            <el-input v-model="form.username" placeholder="请输入账号" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="密码" prop="password">
                            <el-input
                                v-model="form.password"
                                placeholder="请输入密码"
                                v-if="title === '添加数据源'"
                            />
                            <el-input
                                type="password"
                                v-model="form.password"
                                placeholder="请输入密码"
                                v-if="title !== '添加数据源'"
                            />
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="20">
                    <el-col :span="12" v-if="form.datasourceType !== null">
                        <el-form-item label="数据库名称" prop="dbname">
                            <el-input v-model="form.dbname" placeholder="请输入数据库名称" />
                        </el-form-item>
                    </el-col>
                    <el-col
                        :span="12"
                        v-if="form.datasourceType !== null && form.datasourceType !== 2"
                    >
                        <el-form-item label="模式" prop="sid">
                            <el-input v-model="form.sid" placeholder="请输入模式" />
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="数据库IP" prop="ip">
                            <el-input v-model="form.ip" placeholder="请输入数据库IP" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="端口号" prop="port">
                            <el-input v-model="form.port" placeholder="请输入端口号" />
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-row :gutter="20">
                    <el-col :span="24">
                        <el-form-item label="数据连接描述" prop="description">
                            <el-input
                                type="textarea"
                                :min-height="192"
                                v-model="form.description"
                                placeholder="请输入数据连接描述"
                            />
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-row :gutter="20">
                    <el-col :span="24">
                        <el-form-item label="备注">
                            <el-input
                                type="textarea"
                                v-model="form.remark"
                                placeholder="请输入备注"
                                :min-height="192"
                            />
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button size="mini" @click="cancel">取 消</el-button>
                    <el-button type="primary" size="mini" @click="submitForm">确 定</el-button>
                </div>
            </template>
        </el-dialog>

        <!-- 数据源详情对话框 -->
        <el-dialog
            :title="title"
            v-model="openDetail"
            width="800px"
            :append-to="$refs['app-container']"
            draggable
        >
            <template #header="{ close, titleId, titleClass }">
                <span role="heading" aria-level="2" class="el-dialog__title">
                    {{ title }}
                </span>
            </template>
            <el-form ref="daDatasourceRef" :model="form" label-width="110px">
                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="数据连接名称" prop="datasourceName">
                            <div>
                                {{ form.datasourceName }}
                            </div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="数据源类型" prop="datasourceType">
                            <dict-tag :options="datasource_type" :value="form.datasourceType" />
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="账号" prop="username">
                            <div>
                                {{ form.username }}
                            </div>
                        </el-form-item>
                    </el-col>

                    <el-col :span="12">
                        <el-form-item label="密码" prop="password">
                            <div>***********</div>
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="数据库名称" prop="dbname">
                            <div>
                                {{ form.dbname }}
                            </div>
                        </el-form-item>
                    </el-col>

                    <el-col
                        :span="12"
                        v-if="form.datasourceType !== null && form.datasourceType !== 2"
                    >
                        <el-form-item label="模式" prop="sid">
                            <div>
                                {{ form.sid }}
                            </div>
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="数据库IP" prop="ip">
                            <div>
                                {{ form.ip }}
                            </div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="端口号" prop="port">
                            <div>
                                {{ form.port }}
                            </div>
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-row :gutter="20">
                    <el-col :span="24">
                        <el-form-item label="数据连接描述" prop="description">
                            <div>
                                {{ form.description }}
                            </div>
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-row :gutter="20">
                    <el-col :span="24">
                        <el-form-item label="备注" prop="remark">
                            <div>
                                {{ form.remark }}
                            </div>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button size="mini" @click="cancel">关 闭</el-button>
                </div>
            </template>
        </el-dialog>

        <!-- 用户导入对话框 -->
        <el-dialog
            :title="upload.title"
            v-model="upload.open"
            width="800px"
            :append-to="$refs['app-container']"
            draggable
            destroy-on-close
        >
            <el-upload
                ref="uploadRef"
                :limit="1"
                accept=".xlsx, .xls"
                :headers="upload.headers"
                :action="upload.url + '?updateSupport=' + upload.updateSupport"
                :disabled="upload.isUploading"
                :on-progress="handleFileUploadProgress"
                :on-success="handleFileSuccess"
                :auto-upload="false"
                drag
            >
                <el-icon class="el-icon--upload"><upload-filled /></el-icon>
                <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
                <template #tip>
                    <div class="el-upload__tip text-center">
                        <div class="el-upload__tip">
                            <el-checkbox
                                v-model="upload.updateSupport"
                            />是否更新已经存在的数据源数据
                        </div>
                        <span>仅允许导入xls、xlsx格式文件。</span>
                        <el-link
                            type="primary"
                            :underline="false"
                            style="font-size: 12px; vertical-align: baseline"
                            @click="importTemplate"
                            >下载模板</el-link
                        >
                    </div>
                </template>
            </el-upload>
            <template #footer>
                <div class="dialog-footer">
                    <el-button @click="upload.open = false">取 消</el-button>
                    <el-button type="primary" @click="submitFileForm">确 定</el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>

<script setup name="DaDatasource">

    import { listDatasource as listExtDatasource, delDatasource as delExtDatasource, getDatasource as getExtDatasource, addDatasource as addExtDatasource, updateDatasource as updateExtDatasource, getTestConnection as getExtTestConnection } from '@/api/ext/extDatasource/datasource';
    import { getToken } from '@/utils/auth.js';
    import request from '@/utils/request';

    const { proxy } = getCurrentInstance();
    const { datasource_type } = proxy.useDict('datasource_type');
    // 支持所有类型的数据源
    const datasourceTypeOptions = computed(() => {
        return [
            { label: 'MySQL', value: 1 },
            { label: 'Neo4j', value: 2 },
            { label: 'Oracle', value: 3 },
            { label: 'PostgreSQL', value: 4 }
        ];
    });
    const daDatasourceList = ref([]);

    // 列显隐信息
    const columns = ref([
        { key: 1, label: '数据连接名称', visible: true },
        { key: 2, label: '数据源类型', visible: true },
        { key: 3, label: '数据源配置(json字符串)', visible: true },
        { key: 4, label: '数据库IP', visible: true },
        { key: 5, label: '端口号', visible: true },
        { key: 6, label: '数据库表数（预留）', visible: true },
        { key: 7, label: '同步记录数（预留）', visible: true },
        { key: 8, label: '同步数据量大小（预留）', visible: true },
        { key: 9, label: '数据连接描述', visible: true },
        { key: 11, label: '创建人', visible: true },
        { key: 13, label: '创建时间', visible: true },
        { key: 17, label: '备注', visible: true }
    ]);

    const getColumnVisibility = (key) => {
        const column = columns.value.find((col) => col.key === key);
        // 如果没有找到对应列配置，默认显示
        if (!column) return true;
        // 如果找到对应列配置，根据visible属性来控制显示
        return column.visible;
    };

    const open = ref(false);
    const openDetail = ref(false);
    const loading = ref(true);
    const showSearch = ref(true);
    const ids = ref([]);
    const single = ref(true);
    const multiple = ref(true);
    const total = ref(0);
    const title = ref('');
    const defaultSort = ref({ prop: 'createTime', order: 'desc' });
    const router = useRouter();

    /*** 用户导入参数 */
    const upload = reactive({
        // 是否显示弹出层（用户导入）
        open: false,
        // 弹出层标题（用户导入）
        title: '',
        // 是否禁用上传
        isUploading: false,
        // 是否更新已经存在的用户数据
        updateSupport: 0,
        // 设置上传的请求头部
        headers: { Authorization: 'Bearer ' + getToken() },
        // 上传的地址
        url: import.meta.env.VITE_APP_BASE_API + '/da/daDatasource/importData'
    });

    const data = reactive({
        form: {},
        queryParams: {
            pageNum: 1,
            pageSize: 10,
            datasourceName: null,
            datasourceType: null,
            datasourceConfig: null,
            ip: null,
            port: null,
            listCount: null,
            syncCount: null,
            dataSize: null,
            description: null,
            createTime: null
        },
        rules: {
            datasourceName: [{ required: true, message: '数据连接名称不能为空', trigger: 'blur' }],
            datasourceType: [{ required: true, message: '数据源类型不能为空', trigger: 'change' }],
            datasourceConfig: [
                { required: true, message: '数据源配置(json字符串)不能为空', trigger: 'blur' }
            ],
            ip: [{ required: true, message: '数据库IP不能为空', trigger: 'blur' }],
            port: [{ required: true, message: '端口号不能为空', trigger: 'blur' }],
            username: [{ required: true, message: '账号不能为空', trigger: 'blur' }],
            password: [{ required: true, message: '密码不能为空', trigger: 'blur' }],
            dbname: [{ required: true, message: '数据库名称不能为空', trigger: 'blur' }],
            // sid: [{ required: true, message: '模式不能为空', trigger: 'blur' }]
        }
    });

    const { queryParams, form, rules } = toRefs(data);

    /** 查询数据源列表 */
    function getList() {
        loading.value = true;
        // 直接使用 ext_datasource 接口，支持所有类型的数据源
        listExtDatasource(queryParams.value).then((response) => {
            const extPage = (response && response.data) ? response.data : {};
            const extRows = Array.isArray(extPage.rows) ? extPage.rows : [];
            // 将 EXT 列表映射为表格可识别的字段
            const mappedExtRows = extRows.map(r => ({
                id: r.id,
                datasourceName: r.name,
                datasourceType: r.type,
                datasourceConfig: r.connectionConfig,
                ip: r.host,
                port: r.port,
                description: r.remark,
                createBy: r.createBy,
                creatorId: r.creatorId,
                createTime: r.createTime,
                updateBy: r.updateBy,
                updaterId: r.updaterId,
                updateTime: r.updateTime,
                remark: r.remark,
                _source: 'EXT'
            }));
            daDatasourceList.value = mappedExtRows;
            total.value = extPage.total || 0;
            loading.value = false;
        }).catch(() => {
            loading.value = false;
        });
    }

    // 取消按钮
    function cancel() {
        open.value = false;
        openDetail.value = false;
        reset();
    }

    // 表单重置
    function reset() {
        form.value = {
            id: null,
            datasourceName: null,
            datasourceType: null,
            datasourceConfig: null,
            ip: null,
            port: null,
            listCount: null,
            syncCount: null,
            dataSize: null,
            description: null,
            validFlag: null,
            createBy: null,
            creatorId: null,
            createTime: null,
            updateBy: null,
            updaterId: null,
            updateTime: null,
            remark: null
        };
        proxy.resetForm('daDatasourceRef');
    }

    /** 搜索按钮操作 */
    function handleQuery() {
        queryParams.value.pageNum = 1;
        getList();
    }

    /** 重置按钮操作 */
    function resetQuery() {
        proxy.resetForm('queryRef');
        handleQuery();
    }

    // 多选框选中数据
    function handleSelectionChange(selection) {
        ids.value = selection.map((item) => item.id);
        single.value = selection.length != 1;
        multiple.value = !selection.length;
    }

    /** 排序触发事件 */
    function handleSortChange(column, prop, order) {
        queryParams.value.orderByColumn = column.prop;
        queryParams.value.isAsc = column.order;
        getList();
    }

    /** 新增按钮操作 */
    function handleAdd() {
        reset();
        open.value = true;
        title.value = '添加数据源';
    }

    /** 修改按钮操作 */
    function handleUpdate(row) {
        reset();
        const _id = row.id || ids.value;
        // 统一使用 ext_datasource 接口
        getExtDatasource(_id).then((response) => {
            const d = response.data || {};
            // 将 EXT 字段映射回本页面表单字段
            form.value = {
                id: d.id,
                datasourceName: d.name,
                datasourceType: d.type,
                datasourceConfig: d.connectionConfig,
                ip: d.host,
                port: d.port,
                username: d.username,
                password: d.password,
                dbname: d.databaseName,
                sid: d.schema,
                description: d.remark,
                validFlag: d.validFlag,
                delFlag: d.delFlag,
                createBy: d.createBy,
                creatorId: d.creatorId,
                createTime: d.createTime,
                updateBy: d.updateBy,
                updaterId: d.updaterId,
                updateTime: d.updateTime,
                remark: d.remark
            };
            open.value = true;
            title.value = '修改数据源';
        });
    }

    /** 详情按钮操作 */
    function handleDetail(row) {
        reset();
        const _id = row.id || ids.value;
        // 统一使用 ext_datasource 接口
        getExtDatasource(_id).then((response) => {
            const d = response.data || {};
            form.value = {
                id: d.id,
                datasourceName: d.name,
                datasourceType: d.type,
                datasourceConfig: d.connectionConfig,
                ip: d.host,
                port: d.port,
                username: d.username,
                password: d.password,
                dbname: d.databaseName,
                sid: d.schema,
                description: d.remark,
                validFlag: d.validFlag,
                delFlag: d.delFlag,
                createBy: d.createBy,
                creatorId: d.creatorId,
                createTime: d.createTime,
                updateBy: d.updateBy,
                updaterId: d.updaterId,
                updateTime: d.updateTime,
                remark: d.remark
            };
            openDetail.value = true;
            title.value = '数据源详情';
        });
    }

    /** 测试连接按钮操作 */
    function handleTestConnection(row) {
        const _id = row.id || ids.value;
        // 统一使用 ext_datasource 测试接口
        getExtTestConnection(_id).then((res) => {
            if (res && res.code === 200) {
                proxy.$modal.msgSuccess(res.msg || '连接成功');
            } else {
                proxy.$modal.msgError(res.msg || '连接失败');
            }
        });
    }


    // 触发后端上传当前数据源配置到 Nacos
    async function handleUploadToNacos() {
        try {
            const res = await request({ url: '/ext/datasource/uploadToNacos', method: 'post' });
            if (res && res.code === 200) {
                proxy.$modal.msgSuccess(res.msg || '已上传到 Nacos');
            } else {
                proxy.$modal.msgError(res.msg || '上传失败');
            }
        } catch (e) {
            proxy.$modal.msgError('上传失败：' + (e.message || '未知错误'));
        }
    }

    /** 提交按钮 */
    function submitForm() {
        proxy.$refs['daDatasourceRef'].validate((valid) => {
            if (!valid) return;
            
            // 统一使用 ext_datasource 接口
            const payload = {
                id: form.value.id,
                name: form.value.datasourceName,
                type: form.value.datasourceType,
                host: form.value.ip,
                port: form.value.port,
                username: form.value.username,
                password: form.value.password,
                databaseName: form.value.dbname,
                schema: form.value.sid,
                status: 0,
                remark: form.value.description,
                connectionConfig: form.value.datasourceType !== 2 ? JSON.stringify({
                    username: form.value.username,
                    password: form.value.password,
                    dbname: form.value.dbname,
                    sid: form.value.sid
                }) : null
            };
            
            if (form.value.id != null) {
                // 修改
                updateExtDatasource(payload)
                    .then(() => {
                        proxy.$modal.msgSuccess('修改成功');
                        open.value = false;
                        getList();
                    })
                    .catch(() => {});
            } else {
                // 新增
                addExtDatasource(payload)
                    .then(() => {
                        proxy.$modal.msgSuccess('新增成功');
                        open.value = false;
                        getList();
                    })
                    .catch(() => {});
            }
        });
    }

    /** 删除按钮操作 */
    function handleDelete(row) {
        const _ids = row.id || ids.value;
        proxy.$modal
            .confirm('是否确认删除数据连接名称为"' + row.datasourceName + '"的数据项？')
            .then(function () {
                // 统一使用 ext_datasource 删除接口
                return delExtDatasource(_ids);
            })
            .then(() => {
                getList();
                proxy.$modal.msgSuccess('删除成功');
            })
            .catch(() => {});
    }

    /** 导出按钮操作 */
    function handleExport() {
        proxy.download(
            'da/daDatasource/export',
            {
                ...queryParams.value
            },
            `daDatasource_${new Date().getTime()}.xlsx`
        );
    }

    /** ---------------- 导入相关操作 -----------------**/
    /** 导入按钮操作 */
    function handleImport() {
        upload.title = '数据源导入';
        upload.open = true;
    }

    /** 下载模板操作 */
    function importTemplate() {
        proxy.download(
            'system/user/importTemplate',
            {},
            `daDatasource_template_${new Date().getTime()}.xlsx`
        );
    }

    /** 提交上传文件 */
    function submitFileForm() {
        proxy.$refs['uploadRef'].submit();
    }

    /**文件上传中处理 */
    const handleFileUploadProgress = (event, file, fileList) => {
        upload.isUploading = true;
    };

    /** 文件上传成功处理 */
    const handleFileSuccess = (response, file, fileList) => {
        upload.open = false;
        upload.isUploading = false;
        proxy.$refs['uploadRef'].handleRemove(file);
        proxy.$alert(
            "<div style='overflow: auto;overflow-x: hidden;max-height: 70vh;padding: 10px 20px 0;'>" +
                response.msg +
                '</div>',
            '导入结果',
            { dangerouslyUseHTMLString: true }
        );
        getList();
    };
    /** ---------------------------------**/

    function routeTo(link, row) {
        if (link !== '' && link.indexOf('http') !== -1) {
            window.location.href = link;
            return;
        }
        if (link !== '') {
            if (link === router.currentRoute.value.path) {
                window.location.reload();
            } else {
                router.push({
                    path: link,
                    query: {
                        id: row.id
                    }
                });
            }
        }
    }

    queryParams.value.orderByColumn = defaultSort.value.prop;
    queryParams.value.isAsc = defaultSort.value.order;
    getList();
</script>
