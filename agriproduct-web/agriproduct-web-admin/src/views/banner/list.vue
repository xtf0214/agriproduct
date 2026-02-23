<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">轮播图管理</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增轮播图
      </el-button>
    </div>

    <el-table v-loading="loading" :data="bannerList" style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column label="轮播图" width="200">
        <template #default="{ row }">
          <el-image
            v-if="row.imageUrl"
            :src="row.imageUrl"
            style="width: 160px; height: 80px"
            fit="cover"
            :preview-src-list="[row.imageUrl]"
          />
          <span v-else>暂无图片</span>
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" />
      <el-table-column label="链接类型" width="120">
        <template #default="{ row }">
          <el-tag v-if="row.linkType === 1">无</el-tag>
          <el-tag v-else-if="row.linkType === 2" type="success">商品</el-tag>
          <el-tag v-else-if="row.linkType === 3" type="warning">分类</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="linkId" label="链接ID" width="100" />
      <el-table-column prop="sortOrder" label="排序" width="100" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag v-if="row.status === 1" type="success">启用</el-tag>
          <el-tag v-else type="info">禁用</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="handleEdit(row)">
            编辑
          </el-button>
          <el-button link type="danger" size="small" @click="handleDelete(row)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-container">
      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="getList"
        @current-change="getList"
      />
    </div>

    <!-- Add/Edit dialog -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑轮播图' : '新增轮播图'"
      width="600px"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入轮播图标题" />
        </el-form-item>
        <el-form-item label="图片URL" prop="image">
          <el-input v-model="form.image" placeholder="请输入图片URL">
            <template #append>
              <el-button @click="handlePreview">预览</el-button>
            </template>
          </el-input>
          <el-image
            v-if="form.image"
            :src="form.image"
            style="width: 100%; max-height: 200px; margin-top: 10px"
            fit="contain"
            :preview-src-list="[form.image]"
          />
        </el-form-item>
        <el-form-item label="链接类型" prop="linkType">
          <el-radio-group v-model="form.linkType">
            <el-radio :value="1">无</el-radio>
            <el-radio :value="2">商品</el-radio>
            <el-radio :value="3">分类</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="form.linkType > 1" label="链接ID" prop="linkId">
          <el-input
            v-model.number="form.linkId"
            type="number"
            :placeholder="form.linkType === 2 ? '请输入商品ID' : '请输入分类ID'"
          />
        </el-form-item>
        <el-form-item label="排序值" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" :max="9999" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  getBannerListApi,
  addBannerApi,
  updateBannerApi,
  deleteBannerApi
} from '@/api/index'
import type { BannerItem } from '@/api/banner'

const loading = ref(false)
const bannerList = ref<BannerItem[]>([])
const total = ref(0)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10
})

// Dialog
const dialogVisible = ref(false)
const isEdit = ref(false)
const currentEditItem = ref<BannerItem | null>(null)
const submitLoading = ref(false)
const formRef = ref<FormInstance>()

const form = reactive({
  title: '',
  image: '',
  linkType: 1,
  linkId: undefined as number | undefined,
  sortOrder: 0,
  status: 1
})

const rules: FormRules = {
  image: [{ required: true, message: '请输入图片URL', trigger: 'blur' }]
}

async function getList() {
  loading.value = true
  try {
    const res = await getBannerListApi(queryParams)
    if (res.code === 200 && res.data) {
      bannerList.value = res.data.records
      total.value = res.data.total
    }
  } catch (error) {
    ElMessage.error('获取轮播图列表失败')
  } finally {
    loading.value = false
  }
}

function handleAdd() {
  isEdit.value = false
  currentEditItem.value = null
  resetForm()
  dialogVisible.value = true
}

function handleEdit(row: BannerItem) {
  isEdit.value = true
  currentEditItem.value = row
  form.title = row.title
  form.image = row.imageUrl
  form.linkType = row.linkType || 1
  form.linkId = row.linkId
  form.sortOrder = row.sortOrder || 0
  form.status = row.status
  dialogVisible.value = true
}

function resetForm() {
  form.title = ''
  form.image = ''
  form.linkType = 1
  form.linkId = undefined
  form.sortOrder = 0
  form.status = 1
  formRef.value?.clearValidate()
}

function handlePreview() {
  if (form.image) {
    window.open(form.image, '_blank')
  }
}

async function handleSubmit() {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        const data = {
          title: form.title,
          image: form.image,
          linkType: form.linkType,
          linkId: form.linkId,
          sortOrder: form.sortOrder,
          status: form.status
        }

        let res
        if (isEdit.value && currentEditItem.value) {
          res = await updateBannerApi(currentEditItem.value.id, data)
        } else {
          res = await addBannerApi(data)
        }

        if (res.code === 200) {
          ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
          dialogVisible.value = false
          getList()
        }
      } catch (error) {
        ElMessage.error(isEdit.value ? '修改失败' : '新增失败')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

async function handleDelete(row: BannerItem) {
  try {
    await ElMessageBox.confirm('确定要删除该轮播图吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const res = await deleteBannerApi(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      getList()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  getList()
})
</script>
