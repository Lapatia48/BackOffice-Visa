<script setup lang="ts">
import { ref, watch } from 'vue'
const props = defineProps<{ etudiant: { id:number; nom:string; email:string } | null }>()
const emit = defineEmits<{ (e: 'saved'): void; (e: 'cancel'): void }>()

const local = ref({ id: 0, nom: '', email: '' })
const loading = ref(false)
const error = ref<string | null>(null)

watch(() => props.etudiant, (v) => {
  if (v) local.value = { ...v }
}, { immediate: true })

async function submit() {
  if (!local.value.nom || !local.value.email) { error.value = 'Nom et email requis'; return }
  loading.value = true
  try {
    const res = await fetch('/api/etudiants/' + local.value.id, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ nom: local.value.nom, email: local.value.email })
    })
    if (!res.ok) throw new Error(await res.text())
    emit('saved')
  } catch (e:any) {
    error.value = e.message || String(e)
  } finally { loading.value = false }
}
</script>

<template>
  <div class="modal">
    <div class="panel">
      <h3>Modifier étudiant</h3>
      <div class="field">
        <label>Nom</label>
        <input v-model="local.nom" />
      </div>
      <div class="field">
        <label>Email</label>
        <input v-model="local.email" />
      </div>
      <div class="actions">
        <button class="primary" @click="submit" :disabled="loading">Enregistrer</button>
        <button class="ghost" @click="emit('cancel')">Annuler</button>
      </div>
      <div v-if="error" class="error">{{ error }}</div>
    </div>
  </div>
</template>

<style scoped>
.modal { position:fixed; left:0; top:0; right:0; bottom:0; display:flex; align-items:center; justify-content:center; background:rgba(2,6,23,0.45); z-index:60 }
.panel { background:#fff; padding:18px; border-radius:12px; min-width:360px; box-shadow:0 20px 60px rgba(2,6,23,0.25) }
.panel h3 { margin:0 0 12px }
label { display:block; margin-top:8px; color:#334155 }
input { width:100%; padding:8px; border:1px solid #e6eef6; border-radius:8px }
.actions { margin-top:12px }
.primary { background:linear-gradient(90deg,#2563eb,#4f46e5); color:white; border:none; padding:8px 12px; border-radius:8px }
.ghost { margin-left:8px; background:#fff; border:1px solid #cbd5e1; padding:8px 12px; border-radius:8px }
.error { color:#b91c1c; margin-top:8px }
</style>
