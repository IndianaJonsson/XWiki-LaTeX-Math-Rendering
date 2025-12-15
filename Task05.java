// 1

// In the MarkdownParser class where Flexmark is initialized
import com.vsch.flexmark.ext.gitlab.GitLabExtension;
import com.vsch.flexmark.parser.Parser;

// ...

MutableDataSet options = new MutableDataSet();
// ... existing options ...

// ADD THIS: Enable GitLab extension which supports $...$ and $$...$$ math
options.set(Parser.EXTENSIONS, Arrays.asList(
    // ... other extensions ...
    GitLabExtension.create() 
));

// Alternatively, if using the specific Math extension:
// options.set(Parser.EXTENSIONS, Arrays.asList(MathExtension.create()));

Parser parser = Parser.builder(options).build();



// 2

import com.vsch.flexmark.ext.gitlab.GitLabExtension;
import com.vsch.flexmark.util.data.MutableDataSet;
import java.util.Arrays;


// 3


// ... existing options configuration ...

// START OF FIX
// Add GitLab Extension to enable math support ($...$ and $$...$$)
// This parses math nodes *before* italics/bold, preventing the conflict.
options.set(Parser.EXTENSIONS, Arrays.asList(
    // Keep existing extensions if there are any (e.g. WikiLinkExtension)
    // ... existing extensions ...
    GitLabExtension.create()
));[[3](https://www.google.com/url?sa=E&q=https%3A%2F%2Fvertexaisearch.cloud.google.com%2Fgrounding-api-redirect%2FAUZIYQHPat9mQuWCzs184SE_bU98hFQ_GJsp58N1wdNduUDYnqdT6ok9A9Ns-ffJ_zjrvsEAgeNuUXD7Ueh5lD4LB3GePhWeOkR4WEg3JM_DHBjUpedlbwqMQ95wQ1UcpBg7H80dxaR17HdDLc-z9ZUleMjfdvoahOz3IHUy_uqY5M3RCpasnUanKhdQlPN0QgzjXsTHcNqTqrjYhsYfw806S3BC-rdAz7JE2_loOwIBPZaY1MySy54f0F4GkZ6wlhjdeIygzO30)]

// Optional: specific GitLab settings to ensure only Math is affected (if desired)
options.set(GitLabExtension.RENDER_BLOCK_MATH, true);
options.set(GitLabExtension.RENDER_BLOCK_MERMAID, false); // Disable if not needed
// END OF FIX

Parser parser = Parser.builder(options).build();