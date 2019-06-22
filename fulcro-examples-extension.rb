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

    title_html = (attrs.has_key? 'title') ? %(<div class="title">#{attrs['title']}</div>\n) : nil


    html_example = %(
#{title_html}

<button class="inspector" onClick="book.main.focus('VICTORY')">Focus Inspector</button>
<div class="short narrow example" id="VICTORY"></div>
<br/>
)
    create_pass_block parent, html_example, subs: nil
  end
end
