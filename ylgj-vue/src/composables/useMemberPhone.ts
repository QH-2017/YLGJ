// 移动端“手机号即身份”绑定：全站共享，持久化到 localStorage
import { ref } from 'vue'

const STORAGE_KEY = 'mobile_phone'

/** 模块级共享的手机号（跨页面实时同步） */
const phone = ref<string>(localStorage.getItem(STORAGE_KEY) || '')

export function useMemberPhone() {
  /** 绑定/更新手机号（空串则解绑） */
  function bind(p: string) {
    const v = (p || '').trim()
    phone.value = v
    if (v) localStorage.setItem(STORAGE_KEY, v)
    else localStorage.removeItem(STORAGE_KEY)
  }

  /** 是否已绑定 */
  function hasPhone() {
    return !!phone.value
  }

  return { phone, bind, hasPhone }
}
