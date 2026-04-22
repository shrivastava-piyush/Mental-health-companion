cmake_minimum_required(VERSION 3.22)
project(llm_bridge)

set(CMAKE_CXX_STANDARD 17)

# ── llama.cpp ───────────────────────────────────────────────────────────
set(LLAMA_DIR ${CMAKE_CURRENT_SOURCE_DIR}/llama.cpp)
set(GGML_CPU_DIR ${LLAMA_DIR}/ggml/src/ggml-cpu)

set(LLAMA_SOURCES
    ${LLAMA_DIR}/src/llama.cpp
    ${LLAMA_DIR}/src/llama-vocab.cpp
    ${LLAMA_DIR}/src/llama-grammar.cpp
    ${LLAMA_DIR}/src/llama-sampler.cpp
    ${LLAMA_DIR}/src/llama-model-loader.cpp
    ${LLAMA_DIR}/src/llama-model.cpp
    ${LLAMA_DIR}/src/llama-context.cpp
    ${LLAMA_DIR}/src/llama-hparams.cpp
    ${LLAMA_DIR}/src/llama-mmap.cpp
    ${LLAMA_DIR}/src/llama-chat.cpp
    ${LLAMA_DIR}/src/llama-impl.cpp
    ${LLAMA_DIR}/src/llama-model-saver.cpp
    ${LLAMA_DIR}/src/llama-arch.cpp
    ${LLAMA_DIR}/src/llama-kv-cache.cpp
    ${LLAMA_DIR}/src/llama-kv-cache-iswa.cpp
    ${LLAMA_DIR}/src/llama-memory.cpp
    ${LLAMA_DIR}/src/llama-memory-recurrent.cpp
    ${LLAMA_DIR}/src/llama-memory-hybrid.cpp
    ${LLAMA_DIR}/src/llama-memory-hybrid-iswa.cpp
    ${LLAMA_DIR}/src/llama-adapter.cpp
    ${LLAMA_DIR}/src/llama-graph.cpp
    ${LLAMA_DIR}/src/llama-batch.cpp
    ${LLAMA_DIR}/src/llama-io.cpp
    ${LLAMA_DIR}/src/unicode.cpp
    ${LLAMA_DIR}/src/unicode-data.cpp
    ${LLAMA_DIR}/ggml/src/ggml.c
    ${LLAMA_DIR}/ggml/src/ggml.cpp
    ${LLAMA_DIR}/ggml/src/ggml-alloc.c
    ${LLAMA_DIR}/ggml/src/ggml-backend.cpp
    ${LLAMA_DIR}/ggml/src/ggml-backend-dl.cpp
    ${LLAMA_DIR}/ggml/src/ggml-backend-reg.cpp
    ${LLAMA_DIR}/ggml/src/ggml-backend-meta.cpp
    ${GGML_CPU_DIR}/ggml-cpu.c
    ${GGML_CPU_DIR}/ggml-cpu.cpp
    ${GGML_CPU_DIR}/traits.cpp
    ${GGML_CPU_DIR}/vec.cpp
    ${GGML_CPU_DIR}/ops.cpp
    ${GGML_CPU_DIR}/binary-ops.cpp
    ${GGML_CPU_DIR}/unary-ops.cpp
    ${GGML_CPU_DIR}/quants.c
    ${LLAMA_DIR}/ggml/src/ggml-quants.c
    ${LLAMA_DIR}/ggml/src/gguf.cpp
    ${LLAMA_DIR}/ggml/src/ggml-threading.cpp
    ${LLAMA_DIR}/ggml/src/ggml-opt.cpp
    ${LLAMA_DIR}/common/common.cpp
    ${LLAMA_DIR}/common/sampling.cpp
)

# ARCH-SPECIFIC implementations
# We include BOTH and let the compiler/preprocessor handle the overlaps 
# if possible, but actually we should be careful.
# The errors showed that without these, symbols like quantize_row_q4_0 are missing.
# These are defined in the arch-specific quants.c files.
if (CMAKE_ANDROID_ARCH_ABI STREQUAL "arm64-v8a")
    list(APPEND LLAMA_SOURCES
        ${GGML_CPU_DIR}/arch/arm/quants.c
        ${GGML_CPU_DIR}/arch/arm/repack.cpp
        ${GGML_CPU_DIR}/arch/arm/cpu-feats.cpp
    )
    add_definitions(-DGGML_USE_ARM_V82)
elseif (CMAKE_ANDROID_ARCH_ABI STREQUAL "x86_64")
    list(APPEND LLAMA_SOURCES
        ${GGML_CPU_DIR}/arch/x86/quants.c
        ${GGML_CPU_DIR}/arch/x86/repack.cpp
        ${GGML_CPU_DIR}/arch/x86/cpu-feats.cpp
    )
endif()

file(GLOB LLAMA_MODELS_SOURCES ${LLAMA_DIR}/src/models/*.cpp)
list(APPEND LLAMA_SOURCES ${LLAMA_MODELS_SOURCES})
list(REMOVE_DUPLICATES LLAMA_SOURCES)

add_library(llama_core STATIC ${LLAMA_SOURCES})
target_include_directories(llama_core PUBLIC
    ${LLAMA_DIR}/include
    ${LLAMA_DIR}/ggml/include
    ${LLAMA_DIR}/common
    ${LLAMA_DIR}/src
    ${LLAMA_DIR}/ggml/src
    ${LLAMA_DIR}/ggml/src/ggml-cpu
)
target_compile_definitions(llama_core PRIVATE
    GGML_USE_CPU
    NDEBUG
    GGML_VERSION="1"
    GGML_COMMIT="unknown"
)

# ── JNI bridge ──────────────────────────────────────────────────────────
add_library(llm_bridge SHARED llm_bridge.cpp)
target_link_libraries(llm_bridge llama_core log android)
target_include_directories(llm_bridge PRIVATE
    ${LLAMA_DIR}/include
    ${LLAMA_DIR}/ggml/include
    ${LLAMA_DIR}/common
)
