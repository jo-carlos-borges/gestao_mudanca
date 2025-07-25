import { ref } from 'vue'

const snackbar = ref(false)
const toastMessage = ref('')
const color = ref('success')

export function useToast() {
  function showToast(msg: string, type: 'success' | 'error' | 'info' | 'warning' = 'success') {
    toastMessage.value = msg
    color.value = type
    snackbar.value = true
  }

  return {
    snackbar,
    toastMessage,
    color,
    showToast,
  }
}
