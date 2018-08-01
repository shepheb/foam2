/**
 * @license
 * Copyright 2018 The FOAM Authors. All Rights Reserved.
 * http://www.apache.org/licenses/LICENSE-2.0
 */

foam.CLASS({
  package: 'foam.u2.search',
  name: 'DateSearchView',
  extends: 'foam.u2.View',

  documentation: `A SearchView for properties of type Date.`,

  exports: [
    'as data'
  ],

  properties: [
    {
      name: 'property',
      documentation: `The property that this view is filtering by. Should be of
          type Date.`,
      required: true
    },
    {
      class: 'String',
      name: 'qualifier', // TODO: rename if appropriate
      documentation: ``, // TODO
      view: {
        class: 'foam.u2.view.ChoiceView',
        choices: [
          ['True', '--'],
          ['Gt', 'after'],
          ['Lt', 'before'],
        ],
        defaultValue: 'True'
      }
    },
    {
      class: 'Date',
      name: 'date',
      documentation: ``, // TODO
    },
    {
      name: 'predicate',
      documentation: `All SearchViews must have a predicate as required by the
          SearchManager. The SearchManager will read this predicate and use it
          to filter the dao being displayed in the view.`,
      expression: function(qualifier, date) {
        if ( qualifier.length > 0 && date && ! isNaN(date.valueOf()) ) {
          return foam.mlang.predicate[qualifier].create({
            arg1: this.property,
            arg2: new Date(date)
          });
        }
        return foam.mlang.predicate.True.create();
      }
    },
    {
      name: 'name',
      documentation: `Required by SearchManager.`,
      value: 'currency search view'
    }
  ],

  methods: [
    function initE() {
      this
        .addClass(this.myClass())
        .start(this.QUALIFIER)
          .start('div').addClass(this.myClass('carrot')).end()
        .end()
        .add(this.DATE);
    },

    /**
     * Clears the fields to their default values.
     * Required on all SearchViews. Called by ReciprocalSearch.
     */
    function clear() {
      this.qualifier = 'True';
      // TODO: Reset date
    }
  ],

  css: `
    ^ {
      display: flex;
      justify-content: center;
      width: 100%;
    }

    ^ > * + * {
      margin-left: 13px;
    }

    ^ .property-qualifier {
      position: relative;
    }

    ^carrot {
      border-left: 5px solid transparent;
      border-right: 5px solid transparent;
      border-top: 5px solid black;
      position: absolute;
      right: 8px;
      top: 18px;
      z-index: 1;
    }

    ^ .foam-u2-tag-Select {
      background-color: white;
      border-radius: 2px;
      border: 1px solid #dce0e7;
      color: #093649;
      height: 40px;
      padding: 0 20px 0 8px;
      -webkit-appearance: none; /* Fix rounded corners in Chrome on OS X */
    }
  `
});
