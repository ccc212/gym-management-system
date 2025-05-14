import { defineStore } from 'pinia'
import { CompetitionItemControllerService } from '../../../generated'
import type { CompetitionItem } from '../../../generated'

// 定义参数类型
interface FetchParams {
  name?: string;
  type?: number;
  category?: number;
  isTeamCompetition?: number;
  page?: number;
  pageSize?: number;
}

// 定义按类型存储的接口
interface ItemsByType {
  [key: number]: CompetitionItem[];
}

export const competitionItemStore = defineStore('competitionItemStore', {
  state: () => ({
    competitionItems: [] as CompetitionItem[],
    itemsByType: {} as ItemsByType,
    loading: false
  }),
  getters: {
    getCompetitionItems: (state) => state.competitionItems,
    getItemsByType: (state) => (type: number) => state.itemsByType[type] || [],
    isLoading: (state) => state.loading
  },
  actions: {
    async fetchCompetitionItems(params: FetchParams = {}) {
      if (this.loading) return;
      
      this.loading = true;
      try {
        const { name, type, category, isTeamCompetition, page = 1, pageSize = 100 } = params;
        
        const res = await CompetitionItemControllerService.listCompetitionItemUsingGet(
          category,
          isTeamCompetition,
          name,
          page,
          pageSize,
          type
        );
        
        if (res && res.data && res.data.records) {
          this.competitionItems = res.data.records as CompetitionItem[];
          
          // 按类型分类存储
          this.itemsByType = {};
          this.competitionItems.forEach(item => {
            if (item.type !== undefined) {
              if (!this.itemsByType[item.type]) {
                this.itemsByType[item.type] = [];
              }
              this.itemsByType[item.type].push(item);
            }
          });
        }
        
        return this.competitionItems;
      } catch (error) {
        console.error('获取赛事项目列表失败:', error);
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    getItemById(id: number) {
      return this.competitionItems.find(item => item.id === id);
    }
  }
}) 