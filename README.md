# Document Store
A comprehensive document storage and retrieval system that combines efficient in-memory data structures with persistent disk storage. This system provides powerful search capabilities, complete undo/redo functionality, and intelligent memory management - all built from scratch without relying on Java's built-in collections framework.
## Key Features
- **Document Management**
  - Store and retrieve text and binary documents using unique URIs
  - Support for document metadata (key-value pairs)
  - Automatic word indexing and counting for text documents
  - JSON-based persistence for long-term storage
- **Complete Undo System**
  - Undo any operation (put, delete, metadata changes)
  - Undo last action globally or on specific documents
  - Support for multi-document undo operations
  - Command pattern implementation with lambda functions
- **Intelligent Memory Management**
  - **Two-Tier Storage**: Automatic movement between RAM and disk
  - **LRU Eviction**: Least Recently Used algorithm for memory optimization
  - **Configurable Limits**: Set maximum document count and memory usage
  - **Transparent Disk I/O**: Documents seamlessly move to/from disk as needed

## Architechture & Data Structures
- **Custom-Built Components**
  - **HashTable**: Separate chaining collision resolution with dynamic resizing
  - **Trie**: Efficient prefix tree for O(k) search operations
  - **MinHeap**: Priority queue for LRU document tracking
  - **BTree**: Balanced tree for scalable document storage
  - **Stack**: Generic implementation for command history
- **Design Patterns**
  - **Command Pattern**: Encapsulated operations for undo functionality
  - **Persistence Manager**: Abstracted disk I/O operations
  - **Generic Programming**: Type-safe, reusable data structures

## Technical Specifications
- **Core Technologies**
  - **Language**: Java 17
  - **Build System**: Maven
  - **Serialization**: GSON for JSON document persistence
  - **Testing**: JUnit for comprehensive test coverage
- **Performance Characteristics**
  - **Search Complexity**: O(k) for keyword/prefix search (k = word length)
  - **Storage**: Unlimited documents with configurable memory limits
  - **Persistence**: Automatic serialization to disk when memory limits exceeded
  - **Recovery**: Documents automatically loaded from disk when accessed
