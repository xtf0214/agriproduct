import { BASE_URL } from '@/utils/request'
import type { Banner, Category, Product } from '@/types'

type MaybeString = string | null | undefined

export interface ProductApiRaw extends Partial<Product> {
  mainImage?: string
  imageList?: string[]
  detail?: string
  subtitle?: string
}

export interface BannerApiRaw extends Partial<Banner> {
  image?: string
}

function isAbsoluteUrl(url: string) {
  return /^https?:\/\//i.test(url)
}

export function normalizeImageUrl(url: MaybeString): string {
  if (!url) return ''

  const value = String(url).trim().replace(/\\/g, '/')
  if (!value) return ''

  if (value.startsWith('data:')) return value
  if (value.startsWith('//')) return `https:${value}`
  if (isAbsoluteUrl(value)) return value

  const base = BASE_URL.replace(/\/$/, '')
  const path = value.startsWith('/') ? value : `/${value}`
  return `${base}${path}`
}

export function normalizeProduct(raw: ProductApiRaw): Product {
  const imageList = Array.isArray(raw.images)
    ? raw.images
    : Array.isArray(raw.imageList)
      ? raw.imageList
      : []

  const normalizedImages = imageList.map((item) => normalizeImageUrl(item)).filter(Boolean)
  const imageUrl = normalizeImageUrl(raw.imageUrl || raw.mainImage || normalizedImages[0] || '')

  return {
    id: Number(raw.id || 0),
    name: raw.name || '',
    price: Number(raw.price || 0),
    stock: Number(raw.stock || 0),
    sales: Number(raw.sales || 0),
    imageUrl,
    images: normalizedImages,
    description: raw.description || raw.detail || raw.subtitle || '',
    categoryId: raw.categoryId ? Number(raw.categoryId) : undefined,
    merchantId: raw.merchantId ? Number(raw.merchantId) : undefined,
    merchantName: raw.merchantName
  }
}

export function normalizeBanner(raw: BannerApiRaw): Banner {
  return {
    id: Number(raw.id || 0),
    title: raw.title || '',
    imageUrl: normalizeImageUrl(raw.imageUrl || raw.image || ''),
    linkUrl: raw.linkUrl || ''
  }
}

export function normalizeCategory(raw: Category): Category {
  return {
    ...raw,
    icon: normalizeImageUrl(raw.icon || '') || raw.icon,
    children: Array.isArray(raw.children) ? raw.children.map(normalizeCategory) : raw.children
  }
}
