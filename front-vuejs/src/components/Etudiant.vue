<script setup lang="ts">
import { ref } from 'vue'

const nom = ref('')
const email = ref('')
const loading = ref(false)
const error = ref<string | null>(null)

const emit = defineEmits<{ (e: 'created'): void }>()

async function submit() {
  error.value = null
  if (!nom.value || !email.value) {
    error.value = 'Nom et email requis'
    return
  }
  loading.value = true
  try {
    const res = await fetch('/api/etudiants', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ nom: nom.value, email: email.value })
    })
    if (!res.ok) throw new Error(await res.text())
    nom.value = ''
    email.value = ''
    emit('created')
  } catch (e: any) {
    error.value = e.message || String(e)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="card form-card">
    <h3>Créer étudiant</h3>
    <div class="field">
      <label>Nom</label>
      <input v-model="nom" placeholder="Prénom Nom" />
    </div>
    <div class="field">
      <label>Email</label>
      <input v-model="email" placeholder="email@example.com" />
    </div>
    <div class="actions">
      <button class="primary" @click="submit" :disabled="loading">Créer</button>
    </div>
    <div v-if="error" class="error">{{ error }}</div>
  </div>
</template>

<style scoped>
.card { padding:18px; border-radius:12px; background:#fff; box-shadow:0 6px 20px rgba(15,23,42,0.06); width:320px }
.form-card h3 { margin:0 0 12px }
.field { margin-bottom:10px }
label { display:block; font-size:0.85rem; margin-bottom:6px; color:#334155 }
input { width:100%; padding:8px 10px; border:1px solid #e6eef6; border-radius:8px; outline:none }
input:focus { border-color:#60a5fa; box-shadow:0 0 0 4px rgba(96,165,250,0.08) }
.actions { margin-top:12px }
.primary { background:linear-gradient(90deg,#2563eb,#4f46e5); color:white; border:none; padding:8px 12px; border-radius:8px }
.error { color:#b91c1c; margin-top:8px }
</style>
