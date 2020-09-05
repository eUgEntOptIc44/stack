package me.tylerbwong.markdown.compose.visitors

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.ui.text.AnnotatedString
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode

internal object HeaderContentVisitor : Visitor {

    override fun accept(
        node: ASTNode,
        builder: AnnotatedString.Builder,
        content: String,
        inlineTextContent: MutableMap<String, InlineTextContent>,
        continuation: Continuation
    ) {
        node.children
            .dropWhile { it.type == MarkdownTokenTypes.WHITE_SPACE }
            .forEach { builder.continuation(it, content) }
    }
}
