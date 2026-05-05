<script setup lang="ts">
import { computed, ref } from 'vue'
import DemandeurResult from '../components/DemandeurResult.vue'
import SearchInputModal from '../components/SearchInputModal.vue'

type SearchType = 'passeport' | 'carte-resident' | 'demande'

const open = ref(false)
const loading = ref(false)
const error = ref('')
const searchType = ref<SearchType>('passeport')
const result = ref<any | null>(null)

const modalTitle = computed(() => {
  if (searchType.value === 'passeport') return 'Chercher par numéro de passeport'
  if (searchType.value === 'carte-resident') return 'Chercher par numéro de carte résident'
  return 'Chercher par numéro de demande'
})

const modalPlaceholder = computed(() => {
  if (searchType.value === 'passeport') return 'Ex: P123456'
  if (searchType.value === 'carte-resident') return 'Ex: CR0001'
  return 'Ex: 42'
})

function openPopup(type: SearchType) {
  searchType.value = type
  open.value = true
  error.value = ''
}

async function submitSearch(value: string) {
  loading.value = true
  error.value = ''

  try {
    const encoded = encodeURIComponent(value)
    const res = await fetch(`/api/demandeurs/${searchType.value}/${encoded}`)
    const bodyText = await res.text()

    if (!res.ok) {
      const maybeJson = bodyText ? JSON.parse(bodyText) : null
      throw new Error(maybeJson?.message || `Erreur HTTP ${res.status}`)
    }

    result.value = bodyText ? JSON.parse(bodyText) : null
    open.value = false
  } catch (e: any) {
    error.value = e.message || 'Erreur de recherche'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <section class="home-card">
    <h2>Accueil recherche demandeur</h2>
    <p class="subtitle">Sélectionnez une recherche, saisissez la valeur dans le popup, puis consultez les données structurées.</p>

    <div class="links-grid">
      <button class="search-link" @click="openPopup('passeport')">
        Chercher par numéro de passeport
      </button>

      <button class="search-link" @click="openPopup('carte-resident')">
        Chercher par numéro de carte résident
      </button>

      <button class="search-link" @click="openPopup('demande')">
        Chercher par numéro de demande
      </button>
    </div>

    <p v-if="error" class="error">{{ error }}</p>

    <DemandeurResult :data="result" />

    <SearchInputModal
      :open="open"
      :loading="loading"
      :title="modalTitle"
      :placeholder="modalPlaceholder"
      @close="open = false"
      @submit="submitSearch"
    />
  </section>
</template>

<style scoped>
.home-card {
  background: #fff0dc;
  border: 1px solid #c8a679;
  border-radius: 14px;
  padding: 18px;
}

h2 {
  margin: 0;
  color: #5a3921;
}

.subtitle {
  margin: 8px 0 14px;
  color: #6f5036;
}

.links-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
}

.search-link {
  border: 1px solid #bb8f5d;
  background: linear-gradient(180deg, #f5ddbc 0%, #e8c79f 100%);
  color: #4f321b;
  border-radius: 12px;
  font-weight: 700;
  padding: 14px;
  text-align: left;
  cursor: pointer;
}

.search-link:hover {
  filter: brightness(0.97);
}

.error {
  margin-top: 12px;
  color: #842a2a;
  font-weight: 600;
}

@media (max-width: 900px) {
  .links-grid {
    grid-template-columns: 1fr;
  }
}
</style>
