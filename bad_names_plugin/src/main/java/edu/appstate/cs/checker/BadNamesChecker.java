package edu.appstate.cs.checker;

import com.google.auto.service.AutoService;
import com.google.errorprone.BugPattern;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.matchers.Description;
import com.sun.source.tree.*;

import javax.lang.model.element.Name;

import static com.google.errorprone.BugPattern.LinkType.CUSTOM;
import static com.google.errorprone.BugPattern.SeverityLevel.WARNING;

@AutoService(BugChecker.class)
@BugPattern(name = "BadNamesChecker", summary = "Poor-quality identifiers", severity = WARNING, linkType = CUSTOM, link = "https://github.com/plse-Lab/")
public class BadNamesChecker extends BugChecker implements
        BugChecker.IdentifierTreeMatcher,
        BugChecker.MethodInvocationTreeMatcher,
        BugChecker.MethodTreeMatcher,
        BugChecker.WhileLoopTreeMatcher,
        BugChecker.ReturnTreeMatcher,
        BugChecker.ClassTreeMatcher,
        BugChecker.VariableTreeMatcher,
        BugChecker.SwitchTreeMatcher,
        BugChecker.IfTreeMatcher {

    @java.lang.Override
    public Description matchIdentifier(IdentifierTree identifierTree, VisitorState visitorState) {
        // NOTE: This matches identifier uses. Do we want to match these,
        // or just declarations?
        Name identifier = identifierTree.getName();
        return checkName(identifierTree, identifier);
    }

    @Override
    public Description matchMethodInvocation(MethodInvocationTree methodInvocationTree, VisitorState visitorState) {
        // NOTE: Similarly to the above, this matches method names in method
        // calls. Do we want to match these, or just declarations?
        Tree methodSelect = methodInvocationTree.getMethodSelect();

        Name identifier;

        if (methodSelect instanceof MemberSelectTree) {
            identifier = ((MemberSelectTree) methodSelect).getIdentifier();
        } else if (methodSelect instanceof IdentifierTree) {
            identifier = ((IdentifierTree) methodSelect).getName();
        } else {
            throw malformedMethodInvocationTree(methodInvocationTree);
        }

        return checkName(methodInvocationTree, identifier);
    }

    @Override
    public Description matchMethod(MethodTree methodTree, VisitorState visitorState) {
        // MethodTree represents the definition of a method. We want to check the name
        // of this
        // method to see if it is acceptable.

        // TODO: What needs to be done here to check the name of the method?
        if (methodTree.getName().length() > 15) {
            return buildDescription(methodTree)
                    .setMessage(String.format("%s is too long for a method name, shame on you", methodTree.getName()))
                    .build();
        }
        // TODO: Remove this, if needed. This is just here because we need to return a
        // Description.
        return Description.NO_MATCH;
    }

    private Description checkName(Tree tree, Name identifier) {
        // TODO: What other names are a problem? Add checks for them here...
        if (identifier.contentEquals("foo") ||
                identifier.contentEquals("bar") ||
                identifier.contentEquals("test")) {
            return buildDescription(tree)
                    .setMessage(String.format("%s is a bad identifier name", identifier))
                    .build();
        }

        return Description.NO_MATCH;
    }

    private static final IllegalStateException malformedMethodInvocationTree(MethodInvocationTree tree) {
        return new IllegalStateException(String.format("Method name %s is malformed.", tree));
    }

    @Override
    public Description matchIf(IfTree tree, VisitorState state) {
        if (tree.getCondition().toString().equals("false")) {
            return buildDescription(tree)
                    .setMessage(String.format("Dead code: %s", tree))
                    .build();
        }
        // the irony is that this is dead code if the top works
        if (tree.getElseStatement() == null) {
            return buildDescription(tree)
                    .setMessage("Found an if without an else")
                    .build();
        }
        return Description.NO_MATCH;
    }

    @Override
    public Description matchWhileLoop(WhileLoopTree tree, VisitorState state) {
        if (tree.getCondition().toString().equals("false")) {
            return buildDescription(tree)
                    .setMessage(String.format("Infinite loop: %s", tree))
                    .build();
        }
        if (tree.getCondition().toString().equals("false")) {
            return buildDescription(tree)
                    .setMessage(String.format("Unreachable code: %s", tree))
                    .build();
        }
        return Description.NO_MATCH;
    }

    @Override
    public Description matchReturn(ReturnTree tree, VisitorState state) {
        if (tree.toString().matches("null")) {
            return buildDescription(tree)
                    .setMessage(String.format("Try not to return null: %s", tree))
                    .build();
        }
        return Description.NO_MATCH;
    }

    @Override
    public Description matchClass(ClassTree tree, VisitorState state) {
        if (tree.getSimpleName().length() > 20) {
            return buildDescription(tree)
                    .setMessage(String.format("Class name is too long: %s", tree.getSimpleName()))
                    .build();
        }
        if (tree.toString().length() < 3) {
            return buildDescription(tree)
                    .setMessage(String.format("Make your class name more descriptive: %s", tree.getSimpleName()))
                    .build();
        }
        return Description.NO_MATCH;
    }

    @Override
    public Description matchVariable(VariableTree tree, VisitorState state) {
        if (tree.getName().length() > 15) {
            return buildDescription(tree)
                    .setMessage(String.format("Variable name is too long: %s", tree.getName()))
                    .build();
        }
        if (tree.getName().length() < 3) {
            return buildDescription(tree)
                    .setMessage(String.format("Make your variable name more descriptive: %s", tree.getName()))
                    .build();
        }
        return Description.NO_MATCH;
    }

    @Override
    public Description matchSwitch(SwitchTree tree, VisitorState state) {
        if (tree.getCases().isEmpty()) {
            return buildDescription(tree)
                    .setMessage(String.format("Empty switch statement: %s", tree))
                    .build();
        }
        return Description.NO_MATCH;
    }
}
