<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">分类管理</h2>
      <div class="header-actions">
        <el-button type="primary" @click="handleAddTopCategory">
          <el-icon><Plus /></el-icon>
          新增一级分类
        </el-button>
      </div>
    </div>

    <el-table
      v-loading="loading"
      :data="categoryList"
      style="width: 100%"
      row-key="id"
      :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
      default-expand-all
    >
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="分类名称" min-width="150" />
      <el-table-column label="图标" width="100">
        <template #default="{ row }">
          <el-image
            v-if="row.icon"
            :src="row.icon"
            style="width: 40px; height: 40px"
            fit="cover"
            :preview-src-list="[row.icon]"
          />
          <span v-else class="text-gray">-</span>
        </template>
      </el-table-column>
      <el-table-column prop="sortOrder" label="排序" width="100" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag v-if="row.status === 1" type="success">启用</el-tag>
          <el-tag v-else type="info">禁用</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="分类级别" width="120">
        <template #default="{ row }">
          <el-tag v-if="!row.parentId || row.parentId === 0" type="primary">一级分类</el-tag>
          <el-tag v-else type="warning">二级分类</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="280" fixed="right">
        <template #default="{ row }">
          <el-button
            v-if="!row.parentId || row.parentId === 0"
            link
            type="success"
            size="small"
            @click="handleAddSubCategory(row)"
          >
            添加子分类
          </el-button>
          <el-button link type="primary" size="small" @click="handleEdit(row)">
            编辑
          </el-button>
          <el-button link type="danger" size="small" @click="handleDelete(row)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- Add/Edit dialog -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      @closed="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入分类名称" maxlength="20" />
        </el-form-item>
        <el-form-item v-if="currentParentCategory" label="父分类">
          <el-input :value="currentParentCategory.name" disabled />
        </el-form-item>
        <el-form-item label="分类图标" prop="icon">
          <el-upload
            class="icon-uploader"
            :action="uploadAction"
            :headers="uploadHeaders"
            :show-file-list="false"
            :before-upload="beforeUpload"
            :on-success="handleUploadSuccess"
            accept=".jpg,.jpeg,.png,.gif"
          >
            <el-image
              v-if="form.icon"
              :src="form.icon"
              style="width: 60px; height: 60px"
              fit="cover"
              :preview-src-list="[form.icon]"
            />
            <el-icon v-else class="icon-uploader-placeholder"><Picture /></el-icon>
            <template #tip>
              <div class="el-upload__tip">点击上传图标</div>
            </template>
          </el-upload>
          <div v-if="uploadLoading" class="upload-loading">
            <el-icon class="is-loading"><Loading /></el-icon>
            <span>上传中...</span>
          </div>
        </el-form-item>
        <el-form-item label="排序值" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" :max="9999" />
          <span class="form-tip">数字越小越靠前</span>
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
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules, type UploadProps } from 'element-plus'
import {
  getCategoryTreeApi,
  addCategoryApi,
  updateCategoryApi,
  deleteCategoryApi,
  uploadCategoryIconApi
} from '@/api/index'
import type { CategoryItem, CategoryForm } from '@/api/category'
import { getToken } from '@/utils/auth'

const loading = ref(false)
const categoryList = ref<CategoryItem[]>([])

// Upload related
const uploadLoading = ref(false)
const uploadAction = computed(() => {
  // 获取基础URL
  const baseUrl = import.meta.env.VITE_API_BASE_URL || ''
  return `${baseUrl}/admin/category/upload`
})
const uploadHeaders = computed(() => {
  const token = getToken()
  return {
    Authorization: token ? `Bearer ${token}` : ''
  }
})
// Dialog
const dialogVisible = ref(false)
const isEdit = ref(false)
const currentEditItem = ref<CategoryItem | null>(null)
const currentParentCategory = ref<CategoryItem | null>(null)
const submitLoading = ref(false)
const formRef = ref<FormInstance>()

const form = reactive<CategoryForm>({
  name: '',
  parentId: undefined,
  icon: '',
  sortOrder: 0,
  status: 1
})

const dialogTitle = computed(() => {
  if (isEdit.value) return '编辑分类'
  if (currentParentCategory.value) return '新增二级分类'
  return '新增一级分类'
})

const rules: FormRules = {
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' },
    { min: 1, max: 20, message: '分类名称长度为1-20个字符', trigger: 'blur' }
  ]
}

async function getList() {
  loading.value = true
  try {
    const res = await getCategoryTreeApi()
    if (res.code === 200 && res.data) {
      categoryList.value = res.data
    }
  } catch (error) {
    ElMessage.error('获取分类列表失败')
  } finally {
    loading.value = false
  }
}

function handleAddTopCategory() {
  isEdit.value = false
  currentEditItem.value = null
    currentParentCategory.value = null
    resetForm()
    dialogVisible.value = true
}

function handleAddSubCategory(row: CategoryItem) {
  isEdit.value = false
  currentEditItem.value = null
  currentParentCategory.value = row
  resetForm()
    form.parentId = row.id
    dialogVisible.value = true
}
function handleEdit(row: CategoryItem) {
  isEdit.value = true
    currentEditItem.value = row
    currentParentCategory.value = null

    form.name = row.name
    form.parentId = row.parentId || undefined
    form.icon = row.icon || ''
    form.sortOrder = row.sortOrder || 0
    form.status = row.status
    dialogVisible.value = true
}
function resetForm() {
  form.name = ''
  form.parentId = undefined
  form.icon = ''
  form.sortOrder = 0
  form.status = 1
  formRef.value?.clearValidate()
}
function beforeUpload(file: File) {
    const isImage = file.type.startsWith('image/')
    const isLt2M = file.size / 1024 / 1024 < 2
    if (!isImage) {
      ElMessage.error('只能上传图片文件')
      return false
    }
    if (!isLt2M) {
      ElMessage.error('图片大小不能超过 2MB')
      return false
    }
    return true
}
async function handleUploadSuccess(response: any) {
    if (response.code === 200 && response.data) {
      form.icon = response.data.url
      ElMessage.success('上传成功')
    } else {
      ElMessage.error(response.message || '上传失败')
    }
    uploadLoading.value = false
}
async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        const data: CategoryForm = {
          name: form.name.trim(),
          parentId: form.parentId || undefined,
          icon: form.icon || undefined,
          sortOrder: form.sortOrder,
          status: form.status
        }
        let res
        if (isEdit.value && currentEditItem.value) {
          res = await updateCategoryApi(currentEditItem.value.id, data)
        } else {
          res = await addCategoryApi(data)
        }
        if (res.code === 200) {
          ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
          dialogVisible.value = false
          getList()
        } else {
          ElMessage.error(res.message || (isEdit.value ? '修改失败' : '新增失败'))
        }
      } catch (error: any) {
        ElMessage.error(error.message || (isEdit.value ? '修改失败' : '新增失败'))
      } finally {
        submitLoading.value = false
      }
    }
  })
}
async function handleDelete(row: CategoryItem) {
  // 检查是否有子分类
  if (row.children && row.children.length > 0) {
    ElMessage.warning('该分类下存在子分类，请先删除子分类')
    return
  }
  try {
    await ElMessageBox.confirm(
      `确定要删除分类"${row.name}"吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    const res = await deleteCategoryApi(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      getList()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}
onMounted(() => {
  getList()
})
</script>

<style lang="scss" scoped>
.page-container {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  margin: 0;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.text-gray {
  color: #999;
}

.form-tip {
  margin-left: 10px;
  color: #999;
  font-size: 12px;
}

.icon-uploader {
  :deep(.el-upload) {
    border: 1px dashed #d9d9ff;
    border-radius: 4px;
    cursor: pointer;
    width: 100px;
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .icon-uploader-placeholder {
    font-size: 24px;
    color: #c0c4cc;
  }

  :deep(.el-upload-dragger) {
    border-color: #409eff;
  }

  :deep(.el-upload-list) {
    margin: 0;
    padding: 0;
  }

  :deep(.el-upload-list__item) {
    margin: 0;
  }
}

.upload-loading {
  margin-top: 10px;
  color: #409eff;
  display: flex;
  align-items: center;
  gap: 5px;
}
</style>
