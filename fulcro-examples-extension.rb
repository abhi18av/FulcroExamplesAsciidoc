require 'asciidoctor'
require 'asciidoctor/extensions'

class FulcroExamplesMacro < Asciidoctor::Extensions::BlockMacroProcessor
  use_dsl

  named :example

  def process example_title, example_id, file_name

    html = %(
.[[#{example_title}]]<<#{example_title},D3>>
====
++++
<button class="inspector" onClick="book.main.focus('#{example_id}')">Focus Inspector</button>
<div class="short narrow example" id="#{example_id}"></div>
<br/>
++++
[source,clojure,role="source"]
----
include::#{file_name}[]
----
)

    create_pass_block example_title, example_id, file_name, html, subs: nil
  end
end