<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import UpdateEtudiant from '../components/UpdateEtudiant.vue'

const route = useRoute()
const router = useRouter()
const etudiant = ref<{ id: number; nom: string; email: string } | null>(null)
const loading = ref(false)
const error = ref<string | null>(null)

const id = computed(() => Number(route.params.id))

async function loadEtudiant() {
  loading.value = true
  error.value = null
  try {
    const res = await fetch(`/api/etudiants/${id.value}`)
    const payload = await res.text()
    if (!res.ok) throw new Error(payload || `HTTP ${res.status}`)
    etudiant.value = JSON.parse(payload)
  } catch (e: any) {
    error.value = e.message || String(e)
  } finally {
    loading.value = false
  }
}

watch(id, () => loadEtudiant(), { immediate: true })

function saved() {
  router.push('/etudiants')
}

function cancel() {
  router.push('/etudiants')
}
</script>

<template>
  <section class="page-card">
    <p class="eyebrow">Étudiants</p>
    <h2>Modifier l'étudiant</h2>

    <div v-if="loading">Chargement…</div>
    <div v-else-if="error" class="error">{{ error }}</div>
    <UpdateEtudiant v-else :etudiant="etudiant" @saved="saved" @cancel="cancel" />
  </section>
</template>

<style scoped>
.page-card {
  padding: 20px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(15, 23, 42, 0.08);
}

.eyebrow {
  margin: 0 0 4px;
  color: #64748b;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  font-size: 0.78rem;
}

h2 {
  margin: 0 0 16px;
}

.error {
  color: #b91c1c;
}
</style>