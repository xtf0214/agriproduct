import { request } from '@/utils/request'
import type { ApiResponse } from '@/types/api'

// 上传文件
export const uploadFile = (file: File, onProgress?: (percent: number) => void) => {
  return request.upload<ApiResponse<{ url: string }>>(
    import.meta.env.VITE_UPLOAD_URL,
    file,
    onProgress
  )
}
