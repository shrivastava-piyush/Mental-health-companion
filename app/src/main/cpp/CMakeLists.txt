cmake_minimum_required(VERSION 3.22)
project(llm_bridge)

set(CMAKE_CXX_STANDARD 17)

# ── llama.cpp ───────────────────────────────────────────────────────────
# Clone llama.cpp into app/src/main/cpp/llama.cpp before building:
#   git clone --depth 1 https://github.com/ggerganov/llama.cpp.git app/src/main/cpp/llama.cpp
set(LLAMA_DIR ${CMAKE_CURRENT_SOURCE_DIR}/llama.cpp)

# Core llama.cpp sources (minimal set for inference)
set(LLAMA_SOURCES
    ${LLAMA_DIR}/src/llama.cpp
    ${LLAMA_DIR}/src/llama-vocab.cpp
    ${LLAMA_DIR}/src/llama-grammar.cpp
    ${LLAMA_DIR}/src/llama-sampling.cpp
    ${LLAMA_DIR}/ggml/src/ggml.c
    ${LLAMA_DIR}/ggml/src/ggml-alloc.c
    ${LLAMA_DIR}/ggml/src/ggml-backend.c
    ${LLAMA_DIR}/ggml/src/ggml-quants.c
    ${LLAMA_DIR}/common/common.cpp
    ${LLAMA_DIR}/common/sampling.cpp
    ${LLAMA_DIR}/common/grammar-parser.cpp
)

add_library(llama_core STATIC ${LLAMA_SOURCES})
target_include_directories(llama_core PUBLIC
    ${LLAMA_DIR}/include
    ${LLAMA_DIR}/ggml/include
    ${LLAMA_DIR}/common
    ${LLAMA_DIR}/src
    ${LLAMA_DIR}/ggml/src
)
target_compile_definitions(llama_core PRIVATE
    GGML_USE_CPU
    NDEBUG
)

# ── JNI bridge ──────────────────────────────────────────────────────────
add_library(llm_bridge SHARED llm_bridge.cpp)
target_link_libraries(llm_bridge llama_core log android)
target_include_directories(llm_bridge PRIVATE
    ${LLAMA_DIR}/include
    ${LLAMA_DIR}/ggml/include
    ${LLAMA_DIR}/common
)
