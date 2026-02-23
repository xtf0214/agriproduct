<template>
  <div class="page-container">
    <div class="page-content">
      <div class="page-header">
        <el-page-header @back="$router.back()">
          <template #content>
            <span class="page-title">{{ isEdit ? '编辑商品' : '添加商品' }}</span>
          </template>
        </el-page-header>
      </div>
      
      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-width="120px"
        class="product-form"
        v-loading="loading"
      >
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入商品名称" maxlength="100" show-word-limit />
        </el-form-item>
        
        <el-form-item label="商品副标题" prop="subtitle">
          <el-input v-model="formData.subtitle" placeholder="请输入商品副标题" maxlength="200" />
        </el-form-item>
        
        <el-form-item label="商品分类" prop="categoryId">
          <el-select v-model="formData.categoryId" placeholder="请选择分类" style="width: 100%;">
            <el-option
              v-for="item in categoryList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="商品主图" prop="mainImage">
          <el-upload
            class="avatar-uploader"
            :action="uploadUrl"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleMainImageSuccess"
            :before-upload="beforeUpload"
          >
            <el-image v-if="formData.mainImage" :src="formData.mainImage" fit="cover" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        
        <el-form-item label="商品图片" prop="images">
          <el-upload
            :action="uploadUrl"
            :headers="uploadHeaders"
            list-type="picture-card"
            :file-list="imageList"
            :on-success="handleImageSuccess"
            :on-remove="handleImageRemove"
            :before-upload="beforeUpload"
            :limit="5"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        
        <el-form-item label="商品价格" prop="price">
          <el-input-number v-model="formData.price" :min="0" :precision="2" :step="1" />
          <span style="margin-left: 10px;">元</span>
        </el-form-item>
        
        <el-form-item label="原价" prop="originalPrice">
          <el-input-number v-model="formData.originalPrice" :min="0" :precision="2" :step="1" />
          <span style="margin-left: 10px;">元</span>
        </el-form-item>
        
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="formData.stock" :min="0" :step="1" />
        </el-form-item>
        
        <el-form-item label="商品详情" prop="detail">
          <el-input
            v-model="formData.detail"
            type="textarea"
            :rows="6"
            placeholder="请输入商品详情"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            {{ isEdit ? '保存' : '提交' }}
          </el-button>
          <el-button @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, FormInstance, FormRules, UploadFile } from 'element-plus'
import { getProductDetail, addProduct, updateProduct, getCategoryList } from '@/api/product'
import { getToken } from '@/utils/auth'
import type { ProductForm, Category } from '@/types/api'

const route = useRoute()
const router = useRouter()

const isEdit = computed(() => !!route.params.id)
const productId = computed(() => Number(route.params.id))
const uploadUrl = import.meta.env.VITE_UPLOAD_URL
const uploadHeaders = computed(() => ({
  Authorization: `Bearer ${getToken()}`
}))

const formRef = ref<FormInstance>()
const loading = ref(false)
const submitting = ref(false)
const categoryList = ref<Category[]>([])

const formData = reactive<ProductForm>({
  categoryId: 0,
  name: '',
  subtitle: '',
  mainImage: '',
  images: [],
  price: 0,
  originalPrice: 0,
  stock: 0,
  detail: ''
})

const imageList = ref<UploadFile[]>([])

const rules: FormRules = {
  name: [
    { required: true, message: '请输入商品名称', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择商品分类', trigger: 'change' }
  ],
  mainImage: [
    { required: true, message: '请上传商品主图', trigger: 'change' }
  ],
  price: [
    { required: true, message: '请输入商品价格', trigger: 'blur' }
  ],
  stock: [
    { required: true, message: '请输入库存', trigger: 'blur' }
  ]
}

// 获取分类列表
const fetchCategoryList = async () => {
  try {
    const res = await getCategoryList()
    if (res.data) {
      categoryList.value = res.data
    }
  } catch (error) {
    console.error(error)
  }
}

// 获取商品详情
const fetchProductDetail = async () => {
  loading.value = true
  try {
    const res = await getProductDetail(productId.value)
    if (res.data) {
      const product = res.data
      formData.categoryId = product.categoryId
      formData.name = product.name
      formData.subtitle = product.subtitle || ''
      formData.mainImage = product.mainImage
      formData.price = product.price
      formData.originalPrice = product.originalPrice || 0
      formData.stock = product.stock
      formData.detail = product.detail || ''
      
      // 处理图片列表
      if (product.images) {
        const images = JSON.parse(product.images)
        formData.images = images
        imageList.value = images.map((url: string, index: number) => ({
          name: `image-${index}`,
          url
        }))
      }
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 上传前验证
const beforeUpload = (file: File) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2
  
  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

// 主图上传成功
const handleMainImageSuccess = (response: any) => {
  if (response.code === 200) {
    formData.mainImage = response.data.url
    ElMessage.success('上传成功')
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

// 图片上传成功
const handleImageSuccess = (response: any, file: UploadFile, fileList: UploadFile[]) => {
  if (response.code === 200) {
    formData.images = fileList.map(f => f.response?.data?.url || f.url || '')
    ElMessage.success('上传成功')
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

// 图片移除
const handleImageRemove = (file: UploadFile, fileList: UploadFile[]) => {
  formData.images = fileList.map(f => f.url || '')
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        const data = {
          ...formData,
          images: JSON.stringify(formData.images)
        }
        
        if (isEdit.value) {
          await updateProduct(productId.value, data)
          ElMessage.success('保存成功')
        } else {
          await addProduct(data)
          ElMessage.success('添加成功')
        }
        
        router.push('/product/list')
      } catch (error) {
        console.error(error)
      } finally {
        submitting.value = false
      }
    }
  })
}

onMounted(() => {
  fetchCategoryList()
  if (isEdit.value) {
    fetchProductDetail()
  }
})
</script>

<style scoped lang="scss">
.product-form {
  max-width: 800px;
}

.avatar-uploader {
  :deep(.el-upload) {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    
    &:hover {
      border-color: #409EFF;
    }
  }
  
  .el-image {
    width: 148px;
    height: 148px;
    display: block;
  }
  
  .avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 148px;
    height: 148px;
    line-height: 148px;
    text-align: center;
  }
}
</style>
