# Vue 3 + TypeScript + Vite

This template should help get you started developing with Vue 3 and TypeScript in Vite. The template uses Vue 3 `<script setup>` SFCs, check out the [script setup docs](https://v3.vuejs.org/api/sfc-script-setup.html#sfc-script-setup) to learn more.

Learn more about the recommended Project Setup and IDE Support in the [Vue Docs TypeScript Guide](https://vuejs.org/guide/typescript/overview.html#project-setup).

# 使用 openapi-typescript-codegen 生成 TypeScript API 客户端代码
# --input: Swagger/OpenAPI 文档的URL地址
# --output: 生成的代码输出目录
# --client: 使用 axios 作为 HTTP 客户端
npx openapi-typescript-codegen --input http://localhost:5678/v2/api-docs --output ./generated --client axios  