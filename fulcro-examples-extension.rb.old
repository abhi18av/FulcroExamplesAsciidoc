include Asciidoctor

# A block macro that embeds a Fulcro example into the output document
#
# Usage
#
#   example::[example_name, example_id, example_source]
#   example::[victory_name, victory_id, ./src/victory_example.cljs]
#
# NOTE: Possible Improvements
# - take short/narrow as arguments

class FulcroExamplesBlockMacro < Extensions::BlockMacroProcessor
  use_dsl

  named :example

  def process parent, example_name, attrs

    example_name = attrs[1]
    example_id = attrs[2]
    example_source = attrs[3]


    clojure_code = File.read(example_source)

    example_block_html = %(
<div class="exampleblock">
<div class="title">Example <a id="#{example_name}"></a><a href="##{example_name}">#{example_name}</a></div>
<div class="content">
<button class="inspector" onclick="book.main.focus('#{example_id}')">Focus Inspector</button>
<div class="short narrow example" id="#{example_id}"></div>
<br>
<div class="listingblock source">
<div class="content">
  <pre class="highlight">
    <code class="language-clojure" data-lang="clojure"> #{clojure_code}
    </code>
  </pre>
</div>
</div>
</div>
</div>
)
    create_pass_block parent, example_block_html, subs: nil
  end
end
