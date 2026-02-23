import { get, put } from './index'

export interface UserItem {
  userId: number
  username: string
  nickname: string
  phone: string
  avatar?: string
  status: number
  createTime?: string
}

export interface UserListParams {
  pageNum?: number
  pageSize?: number
}

// Get user list
export function getUserListApi(params?: UserListParams) {
  return get<{ records: UserItem[]; total: number }>('/admin/user/list', params)
}

// Update user status
export function updateUserStatusApi(id: number, status: number) {
  return put<boolean>(`/admin/user/${id}/status`, { status })
}
