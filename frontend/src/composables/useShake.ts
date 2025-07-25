import { ref } from 'vue'

export function useShake(duration = 500) {
  const shake = ref(false)

  function triggerShake() {
    shake.value = true
    setTimeout(() => {
      shake.value = false
    }, duration)
  }

  return {
    shake,
    triggerShake,
  }
}
