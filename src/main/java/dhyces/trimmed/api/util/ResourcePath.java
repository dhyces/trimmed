package dhyces.trimmed.api.util;

import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Pattern;

public class ResourcePath {
    private final String namespace;
    private final String[] path;
    private static final Pattern ALLOWED_NAMESPACE = Pattern.compile("[^a-z0-9\\_\\.\\-]");
    private static final Pattern ALLOWED_PATH = Pattern.compile("[^a-z0-9\\/\\_\\.\\-]");

    public ResourcePath(String str) {
        this(split(str));
    }

    public ResourcePath(ResourceLocation resourceLocation) {
        this(resourceLocation.getNamespace(), resourceLocation.getPath());
    }

    public ResourcePath(String namespace, String path) {
        validateNamespace(namespace);
        validatePath(path);
        this.namespace = namespace;
        this.path = path.split("/");
    }

    private ResourcePath(String namespace, String[] pathElements) {
        this.namespace = namespace;
        this.path = pathElements;
    }

    private ResourcePath(String[] split) {
        this(split[0], split[1]);
    }

    protected static String[] split(String str) {
        return str.split(":");
    }

    private void validateNamespace(String namespace) {
        if (ALLOWED_NAMESPACE.matcher(namespace).matches()) {
            throw new IllegalArgumentException("Namespace is invalid. Must be \"[[a-z][0-9][_][.][-]]\"");
        }
    }

    private void validatePath(String path) {
        if (ALLOWED_PATH.matcher(path).matches()) {
            throw new IllegalArgumentException("Path is invalid. Must be \"[[a-z][0-9][/][_][.][-]]\"");
        }
    }

    public ResourcePath getParent() {
        String[] parentPath = new String[path.length-1];
        System.arraycopy(path, 0, parentPath, 0, parentPath.length);
        return new ResourcePath(namespace, parentPath);
    }

    public String getParentElement() {
        return path[path.length-2];
    }

    public String getDirectoryStringFrom(String beginningDirectory) {
        for (int i = 0; i < path.length; i++) {
            String pathElement = path[i];
            if (pathElement.equals(beginningDirectory)) {
                return ofElements(i+1, !path[path.length - 1].contains(".") ? path.length : path.length-1);
            }
        }
        return "";
    }

    public ResourcePath getFileName() {
        return new ResourcePath(namespace, path[path.length-1]);
    }

    public ResourcePath getFileNameOnly() {
        return getFileNameOnly(path[path.length-1].length() - path[path.length-1].indexOf("."));
    }

    public ResourcePath getFileNameOnly(int numExtensionChars) {
        return new ResourcePath(namespace, path[path.length-1].substring(0, path[path.length-1].length()-numExtensionChars));
    }

    public ResourceLocation asResourceLocation() {
        return new ResourceLocation(namespace, getPath());
    }

    public String getNamespace() {
        return namespace;
    }

    public String getPath() {
        return ofElements(0, path.length);
    }

    private String ofElements(int startIndexInclusive, int endIndexExclusive) {
        if (startIndexInclusive == endIndexExclusive || startIndexInclusive+1 == endIndexExclusive) {
            return path[startIndexInclusive];
        }
        StringBuilder builder = new StringBuilder();
        for (int i = startIndexInclusive; i < endIndexExclusive; i++) {
            builder.append(path[i]);
            if (i+1 != endIndexExclusive) {
                builder.append("/");
            }
        }
        return builder.toString();
    }

    @Override
    public String toString() {
        return namespace + ":" + getPath();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResourcePath that = (ResourcePath) o;
        return Objects.equals(namespace, that.namespace) && Arrays.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(namespace);
        result = 31 * result + Arrays.hashCode(path);
        return result;
    }
}
