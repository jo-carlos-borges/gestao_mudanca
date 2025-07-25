<script setup lang="ts">
import type { User } from '@/models/User'
import { ref, watch, computed } from 'vue'
import { nameRules, emailRules, passwordRules, roleRules } from '@/utils/validationRules.ts'
import { useShake } from '@/composables/useShake'

const emit = defineEmits(['update:isOpen', 'saved'])

const props = defineProps<{
  isOpen: boolean
  loading: boolean
  user: User | null
}>()

const internalOpen = ref(props.isOpen)
const formRef = ref()
const name = ref('')
const email = ref('')
const roles = ref<string[]>([])
const password = ref('')
const title = computed(() => (props.user ? 'Editar Usuário' : 'Adicionar Usuário'))

const { shake, triggerShake } = useShake()

watch(
  () => props.isOpen,
  (val) => {
    internalOpen.value = val
    if (internalOpen.value && !props.user) {
      resetForm()
    }
  },
)

watch(internalOpen, (val) => {
  emit('update:isOpen', val)
})

watch(
  () => props.user,
  (user) => {
    if (user) {
      name.value = user.name
      email.value = user.email
      roles.value = [...user.roles]
    }
  },
  { immediate: true },
)

function close() {
  internalOpen.value = false
  //resetForm()
}

async function submit() {
  const result = await formRef.value?.validate()

  if (!result?.valid) {
    triggerShake()
    return
  }

  const payload = {
    id: props.user?.id,
    name: name.value,
    email: email.value,
    roles: roles.value,
    password: password.value,
  }

  emit('saved', payload)
}

function resetForm() {
  name.value = ''
  email.value = ''
  roles.value = []
  password.value = ''
}
</script>

<template>
  <v-dialog v-model="internalOpen" max-width="500" persistent transition="dialog-bottom-transition">
    <v-card :class="{ shake: shake }">
      <v-card-title class="text-h6 font-weight-bold">
        <v-icon class="me-2">mdi-account-plus</v-icon>
        {{ title }}
      </v-card-title>

      <v-card-text>
        <v-form ref="formRef" @submit.prevent="submit" autocomplete="off">
          <v-text-field
            v-model="name"
            label="Nome"
            prepend-inner-icon="mdi-account"
            :rules="nameRules"
            required
          />
          <v-text-field
            v-model="email"
            label="Email"
            prepend-inner-icon="mdi-email"
            type="email"
            autocomplete="new-email"
            :rules="emailRules"
            required
          />
          <v-select
            v-model="roles"
            :items="['ROLE_ADMIN', 'ROLE_USER']"
            label="Permissões"
            prepend-inner-icon="mdi-shield-account"
            multiple
            chips
            :rules="roleRules"
            required
          />
          <v-text-field
            v-if="!props.user"
            v-model="password"
            label="Senha"
            prepend-inner-icon="mdi-lock"
            type="password"
            autocomplete="new-password"
            :rules="passwordRules"
            required
          />
        </v-form>
      </v-card-text>

      <v-card-actions class="justify-end">
        <v-btn variant="tonal" color="grey" @click="close">Cancelar</v-btn>
        <v-btn
          color="primary"
          variant="flat"
          :loading="loading"
          :disabled="loading"
          @click="submit"
        >
          Salvar
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
