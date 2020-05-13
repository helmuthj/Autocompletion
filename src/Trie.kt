// This class is inspired by this guy: https://gist.github.com/sagar-viradiya

// The idea is to load a dictionary into a prefix tree structure for faster retrieval.
// On this structure it is fairly efficient to find sub-trees corresponding to queries.
// Example: The query "ar" will yield a sub-tree that contains words such as "art", "arm", "article", etc.
// The actual words on the sub-tree can later be retrieved one-by-one using the next() method.
// In fact, the Trie class implements next() and hasNext() of the Iterator interface.
class Trie: Iterator<String?> {

    // There are two types of nodes:
    //  - the ones with "word" property set are words
    //  - the ones without "word" property set are intermediate nodes
    // Note: a word node can also be an intermediate node in the sense that it may have child nodes
    data class Node(var word: String? = null, val childNodes: MutableMap<Char, Node> = mutableMapOf())

    // This is a helper class needed for implementing retrieval of leaf nodes without recursion.
    // The querySubtree(), hasNext(), and next() methods use it.
    class NodeStack() {
        private val nodes: MutableList<Node> = mutableListOf<Node>()
        private var last: Int = -1

        fun push(n: Node): Unit {
            nodes.add(n)
            last++
        }

        fun pop(): Node {
            val lastnode: Node = nodes.removeAt(last)
            last--
            return lastnode
        }

        fun clear(): Unit {
            nodes.clear()
            last = -1
        }

        fun empty(): Boolean {
            if (last==-1)
                return true
            return false
        }
    }

    private val root: Node
    private val ns: NodeStack

    init {
        root = Node()
        ns = NodeStack()
        ns.push(root)
    }

    fun insertDictionary(dictionary: List<String>) {
        for (word in dictionary) {
            insert(word)
        }
    }

    // Traverses the trie level-by-level, adding new intermediate and word nodes as required
    private fun insert(word: String) {
        var currentNode = root
        for (char in word) {
            if (currentNode.childNodes[char] == null) {
                currentNode.childNodes[char] = Node()
            }
            currentNode = currentNode.childNodes[char]!!
        }
        // upon reaching the node with the final character of "word",
        // set the node's "word" property to the value of "word"
        currentNode.word = word
    }

    // traverses Trie and tries to find a node that matches "query"
    private fun traverseTo(query: String): Node? {
        var currentNode = root
        for (char in query) {
            if (currentNode.childNodes[char] == null) {
                // "char" is not in a key in the map of the current node->word can't be below
                return null
            }
            currentNode = currentNode.childNodes[char]!!
        }
        return currentNode
    }

    // finds sub-tree for a given query and pushes its root node to the node stack for word retrieval
    fun querySubtree(query: String): Boolean {
        ns.clear()
        val currentNode: Node? = traverseTo(query)

        if(currentNode!=null) {
            // "word" is a node in the Trie, push to stack for later querying,
            // return true to indicate that something was found
            ns.push(currentNode)
            return true
        }
        return false
    }

    // returns the next word on any of the leafs in the sub-tree
    override fun next(): String? {
        // this loop runs until the stack is empty, or a word node is found
        while(!ns.empty()) {
            // take next node from stack
            val node: Node = ns.pop()

            // push all its children onto the stack
            for(cn in node.childNodes.values) {
                ns.push(cn)
            }

            // if it is a word node, return the word
            // NOTE: word need not be leafs. E.g. "art" is a grandparent of "article".
            if(node.word!=null) {
                return node.word!!
            }
        }
        // this should never happen, since we use the iterator logic to check hasNext() in advance
        return null
    }

    // returns "false" if all words in the sub-tree have been retrieved by next() and "true" otherwise
    override fun hasNext(): Boolean {
        return !ns.empty()
    }
}