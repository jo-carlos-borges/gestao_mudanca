import { ref } from 'vue'

const isOpen = ref(false)
const title = ref('')
const message = ref('')
const confirmColor = ref('error')

let confirmCallback: (value: boolean) => void

export function useConfirm() {
  function showConfirm(t: string, m: string, color = 'error') {
    title.value = t
    message.value = m
    confirmColor.value = color
    isOpen.value = true

    return new Promise<boolean>((resolve) => {
      confirmCallback = resolve
    })
  }

  function confirm() {
    isOpen.value = false
    confirmCallback?.(true)
  }

  function cancel() {
    isOpen.value = false
    confirmCallback?.(false)
  }

  return {
    isOpen,
    title,
    message,
    confirmColor,
    showConfirm,
    confirm,
    cancel,
  }
}
