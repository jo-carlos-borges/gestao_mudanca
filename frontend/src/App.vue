<script setup lang="ts">
import { useToast } from '@/composables/useToast'
import { useConfirm } from '@/composables/useConfirm'
const { snackbar, toastMessage, color } = useToast()
const { isOpen, title, message, confirmColor, cancel, confirm } = useConfirm()
</script>

<template>
  <router-view />

  <v-snackbar
    v-model="snackbar"
    :color="color"
    timeout="3000"
    location="top right"
    class="rounded-lg error-snackbar"
  >
    {{ toastMessage }}
  </v-snackbar>

  <v-dialog v-model="isOpen" width="400" transition="slide-x-transition" persistent>
    <v-card class="rounded-xl">
      <v-card-title class="text-h6 font-weight-bold">
        <v-icon color="error" class="me-2">mdi-alert-circle</v-icon>
        {{ title }}
      </v-card-title>
      <v-card-text class="text-body-1">
        {{ message }}
      </v-card-text>
      <v-card-actions class="justify-end">
        <v-btn variant="tonal" color="grey" @click="cancel">Cancelar</v-btn>
        <v-btn :color="confirmColor" variant="flat" @click="confirm">Confirmar</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<style scoped>
.error-snackbar {
  white-space: pre-line;
}
</style>
