import { post, get, upload } from "@/utils/request";
import type { MerchantApplyParams, MerchantInfo } from "@/types";

// 商家入驻申请
export function applyMerchant(data: MerchantApplyParams) {
  return post<number>("/api/merchant/apply", data, { showLoading: true });
}

// 获取当前用户的商家信息
export function getMerchantInfo() {
  return get<MerchantInfo>("/api/merchant/info");
}

// 上传营业执照（使用通用上传接口，因为商家上传接口需要先成为商家）
export function uploadLicense(filePath: string) {
  return upload<{ url: string }>({
    url: "/api/auth/avatar/upload",
    filePath,
    showLoading: true,
  });
}
