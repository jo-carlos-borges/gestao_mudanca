const required =
  (field = 'Campo') =>
  (v: string | unknown) =>
    !!v || `${field} é obrigatório`

const emailRule =
  (field = 'Email') =>
  (v: string) =>
    /.+@.+\..+/.test(v) || `${field} inválido`

const minLength =
  (min: number, field = 'Campo') =>
  (v: string) =>
    (v && v.length >= min) || `${field} precisa ter no mínimo ${min} caracteres`

const maxLength =
  (max: number, field = 'Campo') =>
  (v: string) =>
    (v && v.length <= max) || `${field} pode ter no máximo ${max} caracteres`

const requiredSelection =
  (field = 'Campo') =>
  (v: unknown) =>
    (Array.isArray(v) ? v.length > 0 : !!v) || `${field} é obrigatório`

export const nameRules = [required('Nome'), minLength(3, 'Nome')]
export const emailRules = [required('Email'), emailRule('Email')]
export const passwordRules = [required('Senha'), minLength(8, 'Senha'), maxLength(32, 'Senha')]
export const roleRules = [requiredSelection('Permissão')]
