// This class is inspired by this guy: https://gist.github.com/sagar-viradiya

class Trie: Iterator<String?> {

    // There are two types of nodes:
    //  - the ones with "word" property set are words
    //  - the ones without "word" property set are intermediate nodes
    // Note: a word node can also be an intermediate node in the sense that it may have child nodes
    data class Node(var word: String? = null, val childNodes: MutableMap<Char, Node> = mutableMapOf())

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

    // Traverses the trie level-by-level, adding new nodes as required
    fun insert(word: String) {
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

    // returns "true" if word is a word-node in trie
    fun isKnownWord(query: String): Boolean {
        val node: Node? = traverseTo(query)
        if(node?.word != null)
            return true
        return false
    }

    // returns "true" if word is an intermediate node, but not a word node
    fun isIntermediateNode(query: String): Boolean {
        val node: Node? = traverseTo(query)
        if(node!=null && node.word == null)
            return true
        return false
    }

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

    override fun next(): String? {
        // this loop runs until the stack is empty, or a word node is found
        while(!ns.empty()) {
            // take next node from stack
            val node: Node = ns.pop()

            // if it is a word node, return the word
            if(node.word!=null) {
                return node.word!!
            }
            // if it isn't, push all its child nodes to the stack
            else {
                for(cn in node.childNodes.values) {
                    ns.push(cn)
                }
            }
        }
        // this should never happen, since we use the iterator logic to check hasNext() in advance
        return null
    }

    override fun hasNext(): Boolean {
        return !ns.empty()
    }
}