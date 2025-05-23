import { getCurrentInstance } from "vue";
import type { ComponentInternalInstance } from "vue";

export default function useInstance() {
  const {appContext,proxy} = getCurrentInstance() as ComponentInternalInstance; 
  const global = appContext.config.globalProperties;
  return {global,proxy}
}