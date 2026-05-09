<script setup lang="ts">
import { ref, onMounted } from 'vue'
const etudiants = ref<Array<{ id:number; nom:string; email:string }>>([])
const loading = ref(false)
const error = ref<string | null>(null)

const emit = defineEmits<{ (e: 'edit', payload: { id:number; nom:string; email:string }): void }>()

// async function reload() {
//   loading.value = true
//   error.value = null
//   try {
//     const res = await fetch('/api/etudiants')
//     const payload = await res.text()
//     if (!res.ok) throw new Error(payload || `HTTP ${res.status}`)
//     if (!payload) {
//       etudiants.value = []
//       return
//     }
//     etudiants.value = JSON.parse(payload)
//   } catch (e:any) {
//     error.value = e.message || String(e)
//   } finally {
//     loading.value = false
//   }
// }

async function reload() {
  loading.value = true
  const res = await fetch('/api/etudiants')
  const payload = await res.json()
  etudiants.value = payload
  loading.value = false
}

defineExpose({ reload })

onMounted(() => reload())
</script>

<template>
  <div class="card">
    <h3>Liste étudiants</h3>
    <div v-if="loading">Chargement…</div>
    <div v-if="error" style="color:red">{{ error }}</div>
    <div v-if="!loading && etudiants.length" class="table-wrap">
      <table>
        <thead>
          <tr><th>ID</th><th>Nom</th><th>Email</th><th></th></tr>
        </thead>
        <tbody>
          <tr v-for="e in etudiants" :key="e.id">
            <td>{{ e.id }}</td>
            <td>{{ e.nom }}</td>
            <td class="muted">{{ e.email }}</td>
            <td class="actions"><button @click="emit('edit', e)">Modifier</button></td>
          </tr>
        </tbody>
      </table>
    </div>
    <div v-if="!loading && etudiants.length===0">Aucun étudiant</div>
  </div>
</template>

<style scoped>
.card { padding:18px; border-radius:12px; background: #fff; box-shadow: 0 6px 20px rgba(15,23,42,0.06); }
.table-wrap { overflow:auto }
table { width:100%; border-collapse:collapse; min-width:600px }
thead tr { background:#f8fafc }
th,td { padding:10px 12px; border-bottom:1px solid #eef2f6; text-align:left }
th { color:#374151; font-size:0.85rem; text-transform:uppercase; letter-spacing:0.05em }
td { color:#0f172a }
.muted { color:#64748b; font-size:0.95rem }
.actions { width:120px; text-align:right }
button { padding:6px 10px; border-radius:8px; border:1px solid #cbd5e1; background:#fff; cursor:pointer }
button:hover { background:#f1f5f9 }
</style>
