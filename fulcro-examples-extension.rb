include Asciidoctor

# A block macro that embeds a Fulcro example into the output document
#
# Usage
#
#   gist::12345[]
#   example::victory_example[]
#

class FulcroExamplesBlockMacro < Extensions::BlockMacroProcessor
  use_dsl

  named :example

  def process parent, example_name, attrs

    code_content = File.read("./src/#{example_name}.cljs")

    code_html = %(

<div class="listingblock source">
<div class="content">
<pre class="highlight"><code class="language-clojure" data-lang="clojure">
#{code_content}
</code>
</pre>
</div>
</div>)

    html_example = %(
<button class="inspector" onClick="book.main.focus('#{example_name}')">Focus Inspector</button>
<div class="short narrow example" id="#{example_name}"></div>
<br/>
#{code_html}
)
    create_pass_block parent, html_example, subs: nil
  end
end
